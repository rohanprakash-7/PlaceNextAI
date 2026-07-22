package com.placenextai.service.impl;

import com.placenextai.dto.RecruiterFeedbackRequest;
import com.placenextai.dto.RecruiterFeedbackResponse;
import com.placenextai.dto.StudentFeedbackSummaryResponse;
import com.placenextai.entity.*;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.RecruiterFeedbackRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.ScoreConfigRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EventService;
import com.placenextai.service.RecruiterFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruiterFeedbackServiceImpl implements RecruiterFeedbackService {

    private final RecruiterFeedbackRepository feedbackRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruiterRepository recruiterRepository;
    private final StudentRepository studentRepository;
    private final ScoreConfigRepository scoreConfigRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public RecruiterFeedbackResponse submitFeedback(
            String recruiterEmail, Long applicationId, RecruiterFeedbackRequest request) {

        Recruiter recruiter = recruiterRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with email: " + recruiterEmail));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        assertRecruiterOwnsApplication(recruiter, application);

        FeedbackOutcome outcome;
        try {
            outcome = FeedbackOutcome.valueOf(request.getOutcome().trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Outcome must be ADVANCED, ON_HOLD or REJECTED");
        }

        RecruiterFeedback feedback = feedbackRepository.save(RecruiterFeedback.builder()
                .applicationId(application.getId())
                .studentId(application.getStudent().getId())
                .recruiterId(recruiter.getId())
                .communicationRating(request.getCommunicationRating())
                .technicalRating(request.getTechnicalRating())
                .problemSolvingRating(request.getProblemSolvingRating())
                .cultureFitRating(request.getCultureFitRating())
                .outcome(outcome)
                .comment(request.getComment())
                .build());

        // This event triggers a rescore - the student's PRS updates immediately.
        eventService.record(application.getStudent().getId(), EventType.FEEDBACK_RECEIVED,
                "Feedback received for " + application.getJob().getTitle() + " at " + application.getJob().getCompany());

        return toResponse(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruiterFeedbackResponse> getFeedbackForApplication(String userEmail, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        boolean isOwningStudent = application.getStudent().getEmail().equalsIgnoreCase(userEmail);
        boolean isOwningRecruiter = recruiterRepository.findByEmail(userEmail)
                .map(recruiter -> recruiter.getCompanyName().equalsIgnoreCase(application.getJob().getCompany()))
                .orElse(false);

        if (!isOwningStudent && !isOwningRecruiter) {
            throw new AccessDeniedException("You do not have permission to view this application's feedback");
        }

        return feedbackRepository.findByApplicationIdOrderByCreatedAtDesc(applicationId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentFeedbackSummaryResponse getStudentSummary(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));

        List<RecruiterFeedback> feedback = feedbackRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());

        if (feedback.isEmpty()) {
            return StudentFeedbackSummaryResponse.builder()
                    .totalFeedbackCount(0)
                    .avgCommunication(0)
                    .avgTechnical(0)
                    .avgProblemSolving(0)
                    .avgCultureFit(0)
                    .scoreAdjustment(0)
                    .recentComments(List.of())
                    .build();
        }

        double avgComm = average(feedback, RecruiterFeedback::getCommunicationRating);
        double avgTech = average(feedback, RecruiterFeedback::getTechnicalRating);
        double avgProblem = average(feedback, RecruiterFeedback::getProblemSolvingRating);
        double avgCulture = average(feedback, RecruiterFeedback::getCultureFitRating);
        double overallAverage = (avgComm + avgTech + avgProblem + avgCulture) / 4.0;

        int cap = scoreConfigRepository.findTopByOrderByIdAsc()
                .map(ScoreConfig::getFeedbackAdjustmentCap)
                .orElse(10);
        int adjustment = (int) Math.round((overallAverage - 3.0) / 2.0 * cap);
        adjustment = Math.max(-cap, Math.min(cap, adjustment));

        List<String> comments = feedback.stream()
                .map(RecruiterFeedback::getComment)
                .filter(comment -> comment != null && !comment.isBlank())
                .limit(5)
                .toList();

        return StudentFeedbackSummaryResponse.builder()
                .totalFeedbackCount(feedback.size())
                .avgCommunication(round1(avgComm))
                .avgTechnical(round1(avgTech))
                .avgProblemSolving(round1(avgProblem))
                .avgCultureFit(round1(avgCulture))
                .scoreAdjustment(adjustment)
                .recentComments(comments)
                .build();
    }

    private void assertRecruiterOwnsApplication(Recruiter recruiter, Application application) {
        if (!recruiter.getCompanyName().equalsIgnoreCase(application.getJob().getCompany())) {
            throw new AccessDeniedException("You can only give feedback on applications to your own company's jobs");
        }
    }

    private double average(List<RecruiterFeedback> feedback, java.util.function.ToIntFunction<RecruiterFeedback> extractor) {
        return feedback.stream().mapToInt(extractor).average().orElse(0);
    }

    private double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }

    private RecruiterFeedbackResponse toResponse(RecruiterFeedback feedback) {
        return RecruiterFeedbackResponse.builder()
                .id(feedback.getId())
                .communicationRating(feedback.getCommunicationRating())
                .technicalRating(feedback.getTechnicalRating())
                .problemSolvingRating(feedback.getProblemSolvingRating())
                .cultureFitRating(feedback.getCultureFitRating())
                .outcome(feedback.getOutcome().name())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
