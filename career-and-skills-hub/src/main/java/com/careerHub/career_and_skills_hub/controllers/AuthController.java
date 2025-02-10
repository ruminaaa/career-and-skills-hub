package com.careerHub.career_and_skills_hub.controllers;

import com.careerHub.career_and_skills_hub.dto.AuthResponse;
import com.careerHub.career_and_skills_hub.dto.LoginRequest;
import com.careerHub.career_and_skills_hub.dto.RegisterRequest;

import com.careerHub.career_and_skills_hub.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request); // This calls the login method
        return ResponseEntity.ok(response);
    }

}
