package com.placenextai.repository;

import com.placenextai.entity.MentorSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MentorSlotRepository extends JpaRepository<MentorSlot, Long> {

    List<MentorSlot> findByAlumniIdOrderByStartTimeAsc(Long alumniId);

    List<MentorSlot> findByBookedFalseAndStartTimeAfterOrderByStartTimeAsc(LocalDateTime after);

    List<MentorSlot> findByStudentIdOrderByStartTimeAsc(Long studentId);

    List<MentorSlot> findByAlumniIdAndBookedTrueOrderByStartTimeAsc(Long alumniId);
}
