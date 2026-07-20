package com.placenextai.repository;

import com.placenextai.entity.ResumeVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, Long> {

    List<ResumeVersion> findByStudentIdOrderByVersionNumberDesc(Long studentId);

    Optional<ResumeVersion> findTopByStudentIdOrderByVersionNumberDesc(Long studentId);

    Optional<ResumeVersion> findByIdAndStudentId(Long id, Long studentId);
}
