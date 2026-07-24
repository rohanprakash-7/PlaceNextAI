package com.placenextai.repository;

import com.placenextai.entity.MentorRequest;
import com.placenextai.entity.MentorRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorRequestRepository extends JpaRepository<MentorRequest, Long> {

    List<MentorRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<MentorRequest> findByAlumniIdOrderByCreatedAtDesc(Long alumniId);

    long countByAlumniIdAndStatus(Long alumniId, MentorRequestStatus status);
}
