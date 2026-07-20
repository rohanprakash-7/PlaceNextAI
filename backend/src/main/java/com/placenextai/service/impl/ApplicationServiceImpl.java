package com.placenextai.service.impl;

import com.placenextai.dto.ApplicationRequest;
import com.placenextai.dto.ApplicationResponse;
import com.placenextai.entity.Application;
import com.placenextai.entity.Job;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.Student;
import com.placenextai.exception.DuplicateResourceException;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.entity.EventType;
import com.placenextai.service.ApplicationService;
import com.placenextai.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public ApplicationResponse apply(String studentEmail, ApplicationRequest request) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.getJobId()));

        if (applicationRepository.existsByStudentAndJob(student, job)) {
            throw new DuplicateResourceException("You have already applied to this job");
        }

        Application application = Application.builder()
                .student(student)
                .job(job)
                .build();

        Application saved = applicationRepository.save(application);
        eventService.record(student.getId(), EventType.APPLICATION_SUBMITTED,
                "Applied to " + job.getTitle() + " at " + job.getCompany());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForStudent(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        return applicationRepository.findByStudentOrderByAppliedDateDesc(student).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForRecruiter(String recruiterEmail) {
        Recruiter recruiter = recruiterRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with email: " + recruiterEmail));

        List<Job> companyJobs = jobRepository
                .findByCompanyIgnoreCaseOrderByCreatedDateDesc(recruiter.getCompanyName());

        if (companyJobs.isEmpty()) {
            return List.of();
        }

        return applicationRepository.findByJobInOrderByAppliedDateDesc(companyJobs).stream()
                .map(this::toResponse)
                .toList();
    }

    private ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .company(application.getJob().getCompany())
                .studentId(application.getStudent().getId())
                .studentName(application.getStudent().getFullName())
                .studentEmail(application.getStudent().getEmail())
                .status(application.getStatus().name())
                .appliedDate(application.getAppliedDate())
                .build();
    }
}
