package com.placenextai.repository;

import com.placenextai.entity.RecruiterBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruiterBadgeRepository extends JpaRepository<RecruiterBadge, Long> {

    List<RecruiterBadge> findByRecruiterId(Long recruiterId);

    boolean existsByRecruiterIdAndBadgeId(Long recruiterId, Long badgeId);
}
