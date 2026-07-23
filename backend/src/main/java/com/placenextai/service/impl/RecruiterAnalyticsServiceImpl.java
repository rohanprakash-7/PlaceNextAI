package com.placenextai.service.impl;

import com.placenextai.dto.DepartmentBreakdownResponse;
import com.placenextai.dto.FunnelResponse;
import com.placenextai.dto.SkillDistributionResponse;
import com.placenextai.entity.Application;
import com.placenextai.entity.ApplicationStatus;
import com.placenextai.entity.Job;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.service.RecruiterAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruiterAnalyticsServiceImpl implements RecruiterAnalyticsService {

    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FunnelResponse> funnel(String recruiterEmail) {
        List<Application> applications = companyApplications(recruiterEmail);

        Map<ApplicationStatus, Long> counts = applications.stream()
                .collect(Collectors.groupingBy(Application::getStatus, Collectors.counting()));

        return Arrays.stream(ApplicationStatus.values())
                .map(status -> FunnelResponse.builder()
                        .stage(status.name())
                        .count(counts.getOrDefault(status, 0L))
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDistributionResponse> skillDistribution(String recruiterEmail) {
        Recruiter recruiter = recruiter(recruiterEmail);
        List<Job> companyJobs = jobRepository.findByCompanyIgnoreCaseOrderByCreatedDateDesc(recruiter.getCompanyName());
        List<Application> applications = companyApplications(recruiterEmail);

        Set<String> requiredSkills = companyJobs.stream()
                .flatMap(job -> splitSkills(job.getSkillsRequired()).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Set<String>> applicantSkillSets = applications.stream()
                .map(application -> splitSkills(application.getStudent().getSkills()))
                .toList();

        return requiredSkills.stream()
                .map(skill -> SkillDistributionResponse.builder()
                        .skill(skill)
                        .applicantCount(applicantSkillSets.stream()
                                .filter(skills -> containsIgnoreCase(skills, skill))
                                .count())
                        .requiredCount(companyJobs.stream()
                                .filter(job -> containsIgnoreCase(splitSkills(job.getSkillsRequired()), skill))
                                .count())
                        .build())
                .sorted(Comparator.comparingLong(SkillDistributionResponse::getApplicantCount).reversed())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentBreakdownResponse> departmentBreakdown(String recruiterEmail) {
        List<Application> applications = companyApplications(recruiterEmail);

        Map<String, Long> counts = applications.stream()
                .map(Application::getStudent)
                .collect(Collectors.toMap(Student::getId, Student::getBranch, (a, b) -> a))
                .values().stream()
                .map(branch -> branch == null || branch.isBlank() ? "Unspecified" : branch)
                .collect(Collectors.groupingBy(branch -> branch, Collectors.counting()));

        return counts.entrySet().stream()
                .map(entry -> DepartmentBreakdownResponse.builder()
                        .branch(entry.getKey())
                        .applicantCount(entry.getValue())
                        .build())
                .sorted(Comparator.comparingLong(DepartmentBreakdownResponse::getApplicantCount).reversed())
                .toList();
    }

    private List<Application> companyApplications(String recruiterEmail) {
        Recruiter recruiter = recruiter(recruiterEmail);
        List<Job> companyJobs = jobRepository.findByCompanyIgnoreCaseOrderByCreatedDateDesc(recruiter.getCompanyName());
        if (companyJobs.isEmpty()) {
            return List.of();
        }
        return applicationRepository.findByJobInOrderByAppliedDateDesc(companyJobs);
    }

    private Recruiter recruiter(String email) {
        return recruiterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with email: " + email));
    }

    private Set<String> splitSkills(String raw) {
        if (raw == null || raw.isBlank()) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean containsIgnoreCase(Set<String> haystack, String needle) {
        return haystack.stream().anyMatch(item -> item.equalsIgnoreCase(needle));
    }
}
