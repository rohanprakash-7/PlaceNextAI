package com.placenextai.service.impl;

import com.placenextai.dto.JobRequest;
import com.placenextai.dto.JobResponse;
import com.placenextai.entity.Job;
import com.placenextai.entity.NotificationType;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.AdminRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.JobService;
import com.placenextai.service.NotificationService;
import com.placenextai.service.RecruiterBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private static final int MAX_JOB_ALERT_RECIPIENTS = 200;

    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final RecruiterBadgeService recruiterBadgeService;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAllByOrderByCreatedDateDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        return toResponse(findJob(id));
    }

    @Override
    @Transactional
    public JobResponse createJob(JobRequest request, String creatorEmail) {
        String company = resolveCompany(request, creatorEmail);

        Job job = Job.builder()
                .title(request.getTitle())
                .company(company)
                .location(request.getLocation())
                .description(request.getDescription())
                .salary(request.getSalary())
                .skillsRequired(request.getSkillsRequired())
                .minCgpa(request.getMinCgpa())
                .build();

        Job saved = jobRepository.save(job);

        if (!isAdmin(creatorEmail)) {
            recruiterBadgeService.checkAndAward(findRecruiter(creatorEmail).getId());
        }

        sendJobAlerts(saved);

        return toResponse(saved);
    }

    private void sendJobAlerts(Job job) {
        Set<String> required = splitSkills(job.getSkillsRequired());
        if (required.isEmpty()) {
            return;
        }

        String message = "New role matching your skills: " + job.getTitle() + " at " + job.getCompany();
        int notified = 0;
        for (Student student : studentRepository.findAll()) {
            if (notified >= MAX_JOB_ALERT_RECIPIENTS) {
                break;
            }
            Set<String> studentSkills = splitSkills(student.getSkills());
            boolean matches = required.stream().anyMatch(skill -> containsIgnoreCase(studentSkills, skill));
            if (matches) {
                notificationService.notify(student.getId(), "ROLE_STUDENT", NotificationType.JOB_ALERT,
                        "New job matches your skills", message, "/dashboard/student/applications");
                notified++;
            }
        }
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

    @Override
    @Transactional
    public JobResponse updateJob(Long id, JobRequest request, String editorEmail) {
        Job job = findJob(id);
        assertCanManage(job, editorEmail);

        job.setTitle(request.getTitle());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setSalary(request.getSalary());
        job.setSkillsRequired(request.getSkillsRequired());
        job.setMinCgpa(request.getMinCgpa());
        if (isAdmin(editorEmail) && request.getCompany() != null && !request.getCompany().isBlank()) {
            job.setCompany(request.getCompany());
        }

        return toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void deleteJob(Long id, String editorEmail) {
        Job job = findJob(id);
        assertCanManage(job, editorEmail);
        jobRepository.delete(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getJobsForRecruiter(String recruiterEmail) {
        Recruiter recruiter = findRecruiter(recruiterEmail);
        return jobRepository.findByCompanyIgnoreCaseOrderByCreatedDateDesc(recruiter.getCompanyName()).stream()
                .map(this::toResponse)
                .toList();
    }

    private String resolveCompany(JobRequest request, String creatorEmail) {
        if (isAdmin(creatorEmail)) {
            if (request.getCompany() == null || request.getCompany().isBlank()) {
                throw new IllegalArgumentException("Company is required when an admin creates a job");
            }
            return request.getCompany();
        }
        return findRecruiter(creatorEmail).getCompanyName();
    }

    private void assertCanManage(Job job, String editorEmail) {
        if (isAdmin(editorEmail)) {
            return;
        }
        Recruiter recruiter = findRecruiter(editorEmail);
        if (!job.getCompany().equalsIgnoreCase(recruiter.getCompanyName())) {
            throw new AccessDeniedException("You can only manage jobs posted by your own company");
        }
    }

    private boolean isAdmin(String email) {
        return adminRepository.findByEmail(email).isPresent();
    }

    private Recruiter findRecruiter(String email) {
        return recruiterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with email: " + email));
    }

    private Job findJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .description(job.getDescription())
                .salary(job.getSalary())
                .skillsRequired(job.getSkillsRequired())
                .minCgpa(job.getMinCgpa())
                .createdDate(job.getCreatedDate())
                .build();
    }
}
