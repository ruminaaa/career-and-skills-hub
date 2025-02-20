package com.careerHub.career_and_skills_hub.services;

import com.careerHub.career_and_skills_hub.config.JwtService;
import com.careerHub.career_and_skills_hub.dto.AuthResponse;
import com.careerHub.career_and_skills_hub.dto.LoginRequest;
import com.careerHub.career_and_skills_hub.dto.RegisterRequest;
import com.careerHub.career_and_skills_hub.entity.User;
import com.careerHub.career_and_skills_hub.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Load user by email for authentication


    // Register method
    public AuthResponse register(RegisterRequest request) {
        User user = new User(request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Fix: Pass name as well
        String token = jwtService.generateToken(user.getEmail(), user.getUsername());

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Fix: Pass name as well
        String token = jwtService.generateToken(user.getEmail(), user.getUsername());

        return new AuthResponse(token);
    }



    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        return null;
    }
}
