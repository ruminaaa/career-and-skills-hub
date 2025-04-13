package com.careerHub.career_and_skills_hub.repositories;

import com.careerHub.career_and_skills_hub.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// AnalysisResultRepository.java
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    List<AnalysisResult> findByEmail(String email);

}