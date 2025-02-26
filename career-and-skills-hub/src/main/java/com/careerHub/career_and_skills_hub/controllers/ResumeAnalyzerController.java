package com.careerHub.career_and_skills_hub.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.careerHub.career_and_skills_hub.services.ResumeAnalyzerService;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/resume-analyzer")
@CrossOrigin(origins = "http://localhost:4200") // Allow frontend to make requests
public class ResumeAnalyzerController {



    private static final Logger logger = LoggerFactory.getLogger(ResumeAnalyzerController.class);
    private final ResumeAnalyzerService resumeAnalyzerService;

    @Autowired
    public ResumeAnalyzerController(ResumeAnalyzerService resumeAnalyzerService) {
        this.resumeAnalyzerService = resumeAnalyzerService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(@RequestParam("file") MultipartFile file) {
        logger.info("Received resume analysis request for file: {}", file.getOriginalFilename());

        try {
            // Call the service to analyze the resume by sending it to the AI backend
            String analysisResult = resumeAnalyzerService.analyzeResume(file);
            logger.info("Resume analysis result: {}", analysisResult);

            return ResponseEntity.ok(analysisResult);
        } catch (Exception e) {
            logger.error("Error analyzing resume: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
