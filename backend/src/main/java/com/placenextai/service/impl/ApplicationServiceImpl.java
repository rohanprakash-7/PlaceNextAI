package com.placenextai.service.impl;

import com.placenextai.dto.ApplicationRequest;
import com.placenextai.dto.ApplicationResponse;
import com.placenextai.dto.ApplicationStageResponse;
import com.placenextai.dto.ApplicationTimelineResponse;
import com.placenextai.entity.Application;
import com.placenextai.entity.ApplicationStatus;
import com.placenextai.entity.EventType;
import com.placenextai.entity.Job;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.Student;
import com.placenextai.exception.DuplicateResourceException;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.RecruiterFeedbackRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.ApplicationService;
import com.placenextai.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    // Ordered progression used to render the visual timeline. REJECTED is
    // deliberately excluded here - it is a terminal branch shown separately,
    // not a step every application is expected to pass through.
    private static final List<ApplicationStatus> STAGE_ORDER = List.of(
            ApplicationStatus.APPLIED,
            ApplicationStatus.SHORTLISTED,
            ApplicationStatus.ASSESSMENT,
            ApplicationStatus.TECHNICAL_INTERVIEW,
            ApplicationStatus.HR_INTERVIEW,
            ApplicationStatus.OFFERED,
            ApplicationStatus.HIRED
    );

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final RecruiterFeedbackRepository feedbackRepository;
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

    @Override
    @Transactional
    public ApplicationResponse updateStatus(String recruiterEmail, Long applicationId, String newStatus) {
        Recruiter recruiter = recruiterRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with email: " + recruiterEmail));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        if (!recruiter.getCompanyName().equalsIgnoreCase(application.getJob().getCompany())) {
            throw new AccessDeniedException("You can only update applications for your own company's jobs");
        }

        ApplicationStatus status;
        try {
            status = ApplicationStatus.valueOf(newStatus.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown application status: " + newStatus);
        }

        application.setStatus(status);
        Application saved = applicationRepository.save(application);

        eventService.record(application.getStudent().getId(), EventType.APPLICATION_STATUS_CHANGED,
                application.getJob().getTitle() + " moved to " + status.name().replace("_", " "));

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationTimelineResponse getTimeline(String userEmail, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        boolean isOwningStudent = application.getStudent().getEmail().equalsIgnoreCase(userEmail);
        boolean isOwningRecruiter = recruiterRepository.findByEmail(userEmail)
                .map(recruiter -> recruiter.getCompanyName().equalsIgnoreCase(application.getJob().getCompany()))
                .orElse(false);

        if (!isOwningStudent && !isOwningRecruiter) {
            throw new AccessDeniedException("You do not have permission to view this application");
        }

        ApplicationStatus current = application.getStatus();
        boolean rejected = current == ApplicationStatus.REJECTED;
        int currentIndex = STAGE_ORDER.indexOf(current);

        List<ApplicationStageResponse> stages = STAGE_ORDER.stream()
                .map(stage -> {
                    int stageIndex = STAGE_ORDER.indexOf(stage);
                    boolean reached = !rejected && currentIndex >= 0 && stageIndex <= currentIndex;
                    return ApplicationStageResponse.builder()
                            .status(stage.name())
                            .label(humanize(stage))
                            .reached(reached)
                            .current(stage == current)
                            .build();
                })
                .toList();

        int feedbackCount = feedbackRepository.findByApplicationIdOrderByCreatedAtDesc(applicationId).size();

        return ApplicationTimelineResponse.builder()
                .applicationId(application.getId())
                .jobTitle(application.getJob().getTitle())
                .company(application.getJob().getCompany())
                .currentStatus(current.name())
                .rejected(rejected)
                .stages(stages)
                .feedbackCount(feedbackCount)
                .build();
    }

    private String humanize(ApplicationStatus status) {
        String[] words = status.name().split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(word.charAt(0)).append(word.substring(1).toLowerCase()).append(" ");
        }
        return builder.toString().trim();
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
