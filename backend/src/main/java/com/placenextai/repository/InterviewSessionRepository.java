package com.placenextai.repository;

import com.placenextai.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

    Optional<InterviewSession> findByIdAndStudentId(Long id, Long studentId);

    List<InterviewSession> findTop10ByStudentIdOrderByStartedAtDesc(Long studentId);
}
