package com.careerHub.career_and_skills_hub.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class RecommendationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User Inputs
    private String userClass;
    private String skills;
    private String interests;
    private String hobbies;
    private String passion;
    private String subject;

    // AI Output
    private String result;  // Store career recommendations as comma-separated string

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}