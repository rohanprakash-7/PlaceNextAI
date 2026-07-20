package com.placenextai.repository;

import com.placenextai.entity.PlatformEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PlatformEventRepository extends JpaRepository<PlatformEvent, Long> {

    List<PlatformEvent> findTop20ByStudentIdOrderByCreatedAtDesc(Long studentId);

    long countByStudentIdAndCreatedAtAfter(Long studentId, LocalDateTime after);
}
