package com.placenextai.repository;

import com.placenextai.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCompanyIgnoreCaseOrderByCreatedDateDesc(String company);

    List<Job> findAllByOrderByCreatedDateDesc();

    @Query("SELECT DISTINCT j.company FROM Job j ORDER BY j.company ASC")
    List<String> findDistinctCompanies();
}
