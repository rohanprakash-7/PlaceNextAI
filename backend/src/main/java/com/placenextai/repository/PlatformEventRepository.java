package com.placenextai.repository;

import com.placenextai.entity.EventType;
import com.placenextai.entity.PlatformEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PlatformEventRepository extends JpaRepository<PlatformEvent, Long> {

    List<PlatformEvent> findTop20ByStudentIdOrderByCreatedAtDesc(Long studentId);

    long countByStudentIdAndCreatedAtAfter(Long studentId, LocalDateTime after);

    long countByStudentIdAndEventType(Long studentId, EventType eventType);

    long countByStudentIdAndEventTypeAndCreatedAtAfter(Long studentId, EventType eventType, LocalDateTime after);

    // Powers the 6.6 activity heatmap - one row per calendar day that has at
    // least one event, not one row per event, so the frontend doesn't have to
    // re-aggregate hundreds of raw events client-side.
    @Query("SELECT FUNCTION('DATE', e.createdAt), COUNT(e) FROM PlatformEvent e "
            + "WHERE e.studentId = :studentId AND e.createdAt >= :since "
            + "GROUP BY FUNCTION('DATE', e.createdAt)")
    List<Object[]> countByDaySince(@Param("studentId") Long studentId, @Param("since") LocalDateTime since);
}
