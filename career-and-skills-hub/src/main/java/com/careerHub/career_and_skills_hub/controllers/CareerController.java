package com.careerHub.career_and_skills_hub.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/careers")
public class CareerController {

    private static final Logger logger = LoggerFactory.getLogger(CareerController.class);
    private final RestTemplate restTemplate;

    @Autowired
    public CareerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/recommend")
    public ResponseEntity<?> recommendCareer(@RequestBody Map<String, Object> userData) {
        String flaskUrl = "http://127.0.0.1:5000/recommend"; // Flask API for recommendations
        logger.info("Received recommendation request: {}", userData);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, userData, Map.class);
            logger.info("Flask API Response: {}", response.getBody());
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Error calling Flask API: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/details/{careerName}")
    public ResponseEntity<?> getCareerDetails(@PathVariable String careerName) {
        String flaskUrl = "http://127.0.0.1:5000/career-details/" + careerName;
        logger.info("Fetching details for career: {}", careerName);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(flaskUrl, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Error fetching career details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
