package com.placenextai.repository;

import com.placenextai.entity.Roadmap;
import com.placenextai.entity.RoadmapStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    Optional<Roadmap> findTopByStudentIdAndStatusOrderByCreatedAtDesc(Long studentId, RoadmapStatus status);

    List<Roadmap> findByStudentIdAndStatus(Long studentId, RoadmapStatus status);
}
