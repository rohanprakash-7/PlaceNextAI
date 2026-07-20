package com.placenextai.repository;

import com.placenextai.entity.RoadmapItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapItemRepository extends JpaRepository<RoadmapItem, Long> {

    List<RoadmapItem> findByRoadmapIdOrderByWeekNumberAsc(Long roadmapId);

    Optional<RoadmapItem> findByIdAndRoadmapId(Long id, Long roadmapId);

    long countByRoadmapIdAndCompletedTrue(Long roadmapId);

    long countByRoadmapId(Long roadmapId);
}
