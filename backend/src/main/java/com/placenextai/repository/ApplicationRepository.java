package com.placenextai.repository;

import com.placenextai.entity.Application;
import com.placenextai.entity.Job;
import com.placenextai.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentOrderByAppliedDateDesc(Student student);

    List<Application> findByJobOrderByAppliedDateDesc(Job job);

    List<Application> findByJobInOrderByAppliedDateDesc(List<Job> jobs);

    boolean existsByStudentAndJob(Student student, Job job);
}
