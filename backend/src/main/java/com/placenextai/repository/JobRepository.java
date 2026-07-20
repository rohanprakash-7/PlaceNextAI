package com.placenextai.repository;

import com.placenextai.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCompanyIgnoreCaseOrderByCreatedDateDesc(String company);

    List<Job> findAllByOrderByCreatedDateDesc();
}
