package com.careerHub.career_and_skills_hub.repositories;

import com.careerHub.career_and_skills_hub.entity.RecommendationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, Long> {
    List<RecommendationHistory> findByUserEmail(String email);
}
