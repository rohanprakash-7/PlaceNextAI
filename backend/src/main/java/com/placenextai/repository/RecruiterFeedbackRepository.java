package com.placenextai.repository;

import com.placenextai.entity.RecruiterFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruiterFeedbackRepository extends JpaRepository<RecruiterFeedback, Long> {

    List<RecruiterFeedback> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);

    List<RecruiterFeedback> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    long countByRecruiterId(Long recruiterId);
}
