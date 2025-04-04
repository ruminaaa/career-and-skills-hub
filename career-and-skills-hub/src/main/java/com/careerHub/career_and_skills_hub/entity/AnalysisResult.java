package com.careerHub.career_and_skills_hub.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "resume")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;

    @Column(name = "resume_score")
    private Integer score;

    @Column(name = "recommended_field")
    private String recommendedField;

    @Column(name = "experience_level")
    private String experienceLevel;

    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "top_roles", columnDefinition = "TEXT")
    private String topRoles;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(name = "recommended_courses", columnDefinition = "TEXT")
    private String recommendedCourses;

    // Helper methods for list/map fields
    public List<String> getSkillsList() {
        return skills == null ? List.of() : List.of(skills.split(",\\s*"));
    }

    public void setSkillsList(List<String> skills) {
        this.skills = skills == null ? null : String.join(",", skills);
    }

    public List<String> getCertificationsList() {
        return certifications == null ? List.of() : List.of(certifications.split(",\\s*"));
    }

    public List<String> getTopRolesList() {
        return topRoles == null ? List.of() : List.of(topRoles.split(",\\s*"));
    }

    public List<String> getRecommendedCoursesList() {
        return recommendedCourses == null ? List.of() :
                List.of(recommendedCourses.split(",(?=\\s*https?:)"));
    }
}