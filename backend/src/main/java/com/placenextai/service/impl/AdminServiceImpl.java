package com.placenextai.service.impl;

import com.placenextai.dto.AdminDashboardResponse;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        return AdminDashboardResponse.builder()
                .totalStudents(studentRepository.count())
                .totalRecruiters(recruiterRepository.count())
                .totalJobs(jobRepository.count())
                .totalApplications(applicationRepository.count())
                .build();
    }
}
