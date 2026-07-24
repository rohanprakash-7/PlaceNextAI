package com.placenextai.repository;

import com.placenextai.entity.MentorBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentorBookmarkRepository extends JpaRepository<MentorBookmark, Long> {

    List<MentorBookmark> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    Optional<MentorBookmark> findByStudentIdAndAlumniId(Long studentId, Long alumniId);

    boolean existsByStudentIdAndAlumniId(Long studentId, Long alumniId);
}
