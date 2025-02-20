package com.careerHub.career_and_skills_hub.controllers;

import com.careerHub.career_and_skills_hub.dto.AuthResponse;
import com.careerHub.career_and_skills_hub.dto.LoginRequest;
import com.careerHub.career_and_skills_hub.dto.RegisterRequest;

import com.careerHub.career_and_skills_hub.entity.User;
import com.careerHub.career_and_skills_hub.repositories.UserRepository;
import com.careerHub.career_and_skills_hub.services.AuthService;
import com.careerHub.career_and_skills_hub.config.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, JwtService jwtService, UserRepository userRepository) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // ✅ New Endpoint to get user profile
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", ""); // Remove "Bearer " prefix
        String name = jwtService.extractName(jwtToken); // Extract username from JWT
        Optional<User> user = userRepository.findByName(name); // Fetch user from DB

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get()); // Return user details
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
}
