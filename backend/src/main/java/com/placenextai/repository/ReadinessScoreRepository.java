package com.placenextai.repository;

import com.placenextai.entity.ReadinessScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadinessScoreRepository extends JpaRepository<ReadinessScore, Long> {

    Optional<ReadinessScore> findTopByStudentIdOrderByComputedAtDesc(Long studentId);

    List<ReadinessScore> findTop20ByStudentIdOrderByComputedAtDesc(Long studentId);
}
