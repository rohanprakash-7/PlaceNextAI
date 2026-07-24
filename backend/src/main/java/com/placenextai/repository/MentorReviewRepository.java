package com.placenextai.repository;

import com.placenextai.entity.MentorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentorReviewRepository extends JpaRepository<MentorReview, Long> {

    List<MentorReview> findByAlumniIdOrderByCreatedAtDesc(Long alumniId);

    boolean existsBySlotId(Long slotId);

    long countByAlumniId(Long alumniId);

    @Query("SELECT AVG(r.rating) FROM MentorReview r WHERE r.alumniId = :alumniId")
    Double averageRatingForAlumni(@Param("alumniId") Long alumniId);
}
