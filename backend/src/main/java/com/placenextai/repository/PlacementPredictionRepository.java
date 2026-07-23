package com.placenextai.repository;

import com.placenextai.entity.PlacementPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlacementPredictionRepository extends JpaRepository<PlacementPrediction, Long> {

    Optional<PlacementPrediction> findTopByStudentIdOrderByComputedAtDesc(Long studentId);

    List<PlacementPrediction> findTop20ByStudentIdOrderByComputedAtDesc(Long studentId);
}
