package com.careerHub.career_and_skills_hub.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ChatbotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);
    private final RestTemplate restTemplate;

    @Autowired
    public ChatbotController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Endpoint for chatbot interaction
    @PostMapping("/api/chatbot")
    public ResponseEntity<?> chatWithBot(@RequestBody Map<String, Object> userMessage) {
        String flaskUrl = "http://127.0.0.1:5000/chat"; // Flask API for chatbot interaction
        logger.info("Received chat request: {}", userMessage);

        try {
            // Sending POST request to Flask API with user message
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, userMessage, Map.class);
            logger.info("Flask API Response: {}", response.getBody());

            // Returning response from Flask API to the client (Angular frontend)
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Error calling Flask API: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
