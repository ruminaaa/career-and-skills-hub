package com.careerHub.career_and_skills_hub.controllers;

import com.careerHub.career_and_skills_hub.config.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/chat")
public class ChatbotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);
    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private static final String FLASK_BASE_URL = "http://localhost:5000";

    @Autowired
    public ChatbotController(RestTemplate restTemplate, JwtService jwtService) {
        this.restTemplate = restTemplate;
        this.jwtService = jwtService;
    }

    @PostMapping("/bot")
    public ResponseEntity<?> chatWithBot(
            @RequestBody Map<String, Object> userMessage,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractEmail(token);
            userMessage.put("user_id", userEmail);

            String url = UriComponentsBuilder.fromHttpUrl(FLASK_BASE_URL + "/chat")
                    .toUriString();

            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    userMessage,
                    String.class
            );
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            logger.error("Chat error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Chat service unavailable");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractEmail(token);

            String url = UriComponentsBuilder.fromHttpUrl(FLASK_BASE_URL + "/history/{userId}")
                    .buildAndExpand(userEmail)
                    .toUriString();

            return restTemplate.getForEntity(url, String.class);

        } catch (Exception e) {
            logger.error("History error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error fetching history");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveConversation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody Map<String, Object> conversationData) {

        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtService.extractEmail(token);

            String url = UriComponentsBuilder.fromHttpUrl(FLASK_BASE_URL + "/save/{userId}")
                    .buildAndExpand(userEmail)
                    .toUriString();

            return restTemplate.postForEntity(url, conversationData, String.class);

        } catch (Exception e) {
            logger.error("Save error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error saving conversation");
        }
    }
    @GetMapping("/conversations")
    public ResponseEntity<?> getUserConversations(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String userEmail = jwtService.extractEmail(token);

        String url = UriComponentsBuilder.fromHttpUrl(FLASK_BASE_URL + "/conversations/{userId}")
                .buildAndExpand(userEmail)
                .toUriString();

        return restTemplate.getForEntity(url, String.class);
    }
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<?> getConversation(
            @PathVariable String conversationId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String userEmail = jwtService.extractEmail(token);

        String url = UriComponentsBuilder.fromHttpUrl(FLASK_BASE_URL + "/conversation/{userId}/{conversationId}")
                .buildAndExpand(userEmail, conversationId)
                .toUriString();

        return restTemplate.getForEntity(url, String.class);
    }
}