package com.careerHub.career_and_skills_hub.controllers;

import com.careerHub.career_and_skills_hub.dto.RecommendationHistoryDTO;
import com.careerHub.career_and_skills_hub.entity.RecommendationHistory;
import com.careerHub.career_and_skills_hub.entity.User;
import com.careerHub.career_and_skills_hub.repositories.RecommendationHistoryRepository;
import com.careerHub.career_and_skills_hub.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationHistoryController {

    private final RecommendationHistoryRepository historyRepo;
    private final UserRepository userRepo;

    public RecommendationHistoryController(RecommendationHistoryRepository historyRepo,
                                           UserRepository userRepo) {
        this.historyRepo = historyRepo;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<?> saveRecommendation(
            @RequestBody RecommendationHistoryDTO dto,
            Authentication authentication
    ) {
        // Get authenticated user
        User user = (User) authentication.getPrincipal();

        return userRepo.findByEmail(user.getEmail())
                .map(foundUser -> {
                    RecommendationHistory history = new RecommendationHistory();
                    // Map DTO to Entity
                    history.setUserClass(dto.getUserClass());
                    history.setSkills(dto.getSkills());
                    history.setInterests(dto.getInterests());
                    history.setHobbies(dto.getHobbies());
                    history.setPassion(dto.getPassion());
                    history.setSubject(dto.getSubject());
                    history.setResult(dto.getResult());
                    history.setDate(LocalDateTime.now());
                    history.setUser(foundUser);

                    historyRepo.save(history);
                    return ResponseEntity.ok("Recommendation saved successfully");
                })
                .orElse(ResponseEntity.status(404).body("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<RecommendationHistory>> getHistory(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(historyRepo.findByUserEmail(user.getEmail()));
    }
}