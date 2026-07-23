package com.placenextai.repository;

import com.placenextai.entity.InterviewExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewExperienceRepository extends JpaRepository<InterviewExperience, Long> {

    List<InterviewExperience> findByCompanyIgnoreCaseOrderByCreatedAtDesc(String company);

    List<InterviewExperience> findByAlumniIdOrderByCreatedAtDesc(Long alumniId);
}
