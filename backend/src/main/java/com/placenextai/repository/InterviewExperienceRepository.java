package com.placenextai.repository;

import com.placenextai.entity.InterviewExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterviewExperienceRepository extends JpaRepository<InterviewExperience, Long> {

    List<InterviewExperience> findByCompanyIgnoreCaseOrderByCreatedAtDesc(String company);

    List<InterviewExperience> findByAlumniIdOrderByCreatedAtDesc(Long alumniId);

    List<InterviewExperience> findAllByOrderByCreatedAtDesc();

    @Query("SELECT e FROM InterviewExperience e WHERE "
            + "(:company IS NULL OR LOWER(e.company) = LOWER(:company)) AND "
            + "(:search IS NULL OR LOWER(e.content) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(e.roleTitle) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(e.company) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "ORDER BY e.createdAt DESC")
    List<InterviewExperience> search(@Param("company") String company, @Param("search") String search);

    @Query("SELECT DISTINCT e.company FROM InterviewExperience e ORDER BY e.company")
    List<String> findDistinctCompanies();
}
