package com.careerHub.career_and_skills_hub.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RecommendationHistoryResponse {
    private String result;
    private String skills;
    private String interests;
    private String hobbies;
    private String passion;
    private String subject;
    private LocalDateTime date;

    public RecommendationHistoryResponse(String result, String skills, String interests,
                                         String hobbies, String passion, String subject,
                                         LocalDateTime date) {
        this.result = result;
        this.skills = skills;
        this.interests = interests;
        this.hobbies = hobbies;
        this.passion = passion;
        this.subject = subject;
        this.date = date;
    }
}