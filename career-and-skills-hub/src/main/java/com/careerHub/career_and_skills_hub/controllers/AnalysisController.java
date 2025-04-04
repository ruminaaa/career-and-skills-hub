package com.careerHub.career_and_skills_hub.controllers;

import com.careerHub.career_and_skills_hub.entity.AnalysisResult;
import com.careerHub.career_and_skills_hub.repositories.AnalysisResultRepository;
import com.careerHub.career_and_skills_hub.config.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/resume")
public class AnalysisController {

    @Autowired
    private AnalysisResultRepository repository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Resume file is empty"));
            }

            // Call Flask service
            Map<String, Object> flaskResponse = callFlaskAnalysisService(file, authHeader);

            // Extract the analysis data from Flask response
            Map<String, Object> analysisData = (Map<String, Object>) flaskResponse.get("analysis");

            // Save to database
            AnalysisResult savedResult = saveAnalysisResult(analysisData, authHeader);

            return ResponseEntity.ok(savedResult);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Analysis failed",
                            "details", e.getMessage()));
        }
    }

    private Map<String, Object> callFlaskAnalysisService(
            MultipartFile file, String authHeader) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", authHeader);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        return restTemplate.postForObject(
                "http://localhost:5000/analyze",
                new HttpEntity<>(body, headers),
                Map.class);
    }

    private AnalysisResult saveAnalysisResult(
            Map<String, Object> analysisData, String authHeader) throws JsonProcessingException {

        AnalysisResult analysis = new AnalysisResult();

        // Extract user email from JWT
        String userEmail = jwtService.extractEmail(authHeader.replace("Bearer ", ""));

        // Map all fields from analysis data
        analysis.setName(getString(analysisData, "name", "Unknown"));
        analysis.setEmail(getString(analysisData, "email", userEmail));
        analysis.setPhone(getString(analysisData, "phone", null));
        analysis.setScore(getInteger(analysisData, "score", 0));

        // Handle list fields
        analysis.setSkills(convertListToString((List<?>) analysisData.get("skills")));
        analysis.setCertifications(convertListToString((List<?>) analysisData.get("certifications")));
        analysis.setTopRoles(convertListToString((List<?>) analysisData.get("top_roles")));
        analysis.setRecommendedCourses(convertListToString((List<?>) analysisData.get("recommended_courses")));

        // Handle complex fields
        analysis.setMissingSkills(objectMapper.writeValueAsString(analysisData.get("missing_skills")));

        // Set derived fields
        analysis.setRecommendedField(determineRecommendedField((List<String>) analysisData.get("top_roles")));
        analysis.setExperienceLevel(determineExperienceLevel(analysis.getScore()));
        analysis.setTimestamp(LocalDateTime.now());

        return repository.save(analysis);
    }

    // Helper methods
    private String getString(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        return (value != null) ? value.toString() : defaultValue;
    }

    private Integer getInteger(Map<String, Object> data, String key, Integer defaultValue) {
        try {
            Object value = data.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return (value != null) ? Integer.parseInt(value.toString()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String convertListToString(List<?> list) {
        return (list == null || list.isEmpty()) ? null : String.join(",", list.stream()
                .map(Object::toString)
                .toArray(String[]::new));
    }

    private String determineRecommendedField(List<String> topRoles) {
        return (topRoles == null || topRoles.isEmpty()) ?
                "General IT" : String.join(", ", topRoles);
    }

    private String determineExperienceLevel(int score) {
        if (score < 30) return "Fresher";
        if (score < 70) return "Intermediate";
        return "Experienced";
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResult> getAnalysisById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    public ResponseEntity<List<AnalysisResult>> getUserAnalyses(
            @RequestHeader("Authorization") String token) {
        String userEmail = jwtService.extractEmail(token.replace("Bearer ", ""));
        return ResponseEntity.ok(repository.findByEmail(userEmail));
    }
}