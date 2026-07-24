package com.placenextai.repository;

import com.placenextai.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findBySessionIdOrderByQuestionOrderAsc(Long sessionId);

    Optional<InterviewQuestion> findByIdAndSessionId(Long id, Long sessionId);
}
