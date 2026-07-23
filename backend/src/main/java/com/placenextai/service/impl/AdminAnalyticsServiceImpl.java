package com.placenextai.service.impl;

import com.placenextai.dto.AdminAnalyticsOverviewResponse;
import com.placenextai.dto.DepartmentAnalyticsResponse;
import com.placenextai.dto.RecruiterActivityResponse;
import com.placenextai.dto.RiskDistributionResponse;
import com.placenextai.entity.Job;
import com.placenextai.entity.PlacementStatus;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.RiskLevel;
import com.placenextai.entity.Student;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.ReadinessScoreRepository;
import com.placenextai.repository.RecruiterFeedbackRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.AdminAnalyticsService;
import com.placenextai.service.PlacementPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final ReadinessScoreRepository readinessScoreRepository;
    private final RecruiterFeedbackRepository feedbackRepository;
    private final PlacementPredictionService placementPredictionService;

    @Override
    @Transactional(readOnly = true)
    public AdminAnalyticsOverviewResponse overview() {
        List<Student> students = studentRepository.findAll();
        long placed = students.stream()
                .filter(student -> student.getPlacementStatus() == PlacementStatus.PLACED)
                .count();

        return AdminAnalyticsOverviewResponse.builder()
                .totalStudents(students.size())
                .placedStudents(placed)
                .placementPercent(students.isEmpty() ? 0 : round1(100.0 * placed / students.size()))
                .averageReadiness(averageReadiness(students))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentAnalyticsResponse> departments() {
        List<Student> students = studentRepository.findAll();
        Map<String, List<Student>> byBranch = students.stream()
                .collect(Collectors.groupingBy(student ->
                        student.getBranch() == null || student.getBranch().isBlank() ? "Unspecified" : student.getBranch()));

        return byBranch.entrySet().stream()
                .map(entry -> DepartmentAnalyticsResponse.builder()
                        .branch(entry.getKey())
                        .studentCount(entry.getValue().size())
                        .averageReadiness(averageReadiness(entry.getValue()))
                        .build())
                .sorted((a, b) -> Long.compare(b.getStudentCount(), a.getStudentCount()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruiterActivityResponse> recruiterActivity() {
        List<Recruiter> recruiters = recruiterRepository.findAll();

        return recruiters.stream()
                .map(recruiter -> {
                    List<Job> companyJobs = jobRepository
                            .findByCompanyIgnoreCaseOrderByCreatedDateDesc(recruiter.getCompanyName());
                    long applicationsReceived = companyJobs.isEmpty()
                            ? 0
                            : applicationRepository.findByJobInOrderByAppliedDateDesc(companyJobs).size();

                    return RecruiterActivityResponse.builder()
                            .recruiterId(recruiter.getId())
                            .companyName(recruiter.getCompanyName())
                            .recruiterName(recruiter.getRecruiterName())
                            .feedbackCount(feedbackRepository.countByRecruiterId(recruiter.getId()))
                            .applicationsReceived(applicationsReceived)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getApplicationsReceived(), a.getApplicationsReceived()))
                .toList();
    }

    @Override
    @Transactional
    public RiskDistributionResponse riskDistribution() {
        List<Student> students = studentRepository.findAll();
        long low = 0;
        long medium = 0;
        long high = 0;

        for (Student student : students) {
            RiskLevel risk = RiskLevel.valueOf(
                    placementPredictionService.getOrComputeLatest(student).getRiskLevel());
            switch (risk) {
                case LOW -> low++;
                case MEDIUM -> medium++;
                case HIGH -> high++;
            }
        }

        return RiskDistributionResponse.builder().low(low).medium(medium).high(high).build();
    }

    private double averageReadiness(List<Student> students) {
        if (students.isEmpty()) {
            return 0;
        }
        double sum = students.stream()
                .mapToInt(student -> readinessScoreRepository
                        .findTopByStudentIdOrderByComputedAtDesc(student.getId())
                        .map(score -> score.getTotalScore())
                        .orElse(0))
                .sum();
        return round1(sum / students.size());
    }

    private double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
