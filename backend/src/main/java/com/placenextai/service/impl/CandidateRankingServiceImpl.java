package com.placenextai.service.impl;

import com.placenextai.dto.PlacementPredictionResponse;
import com.placenextai.dto.RankedCandidateResponse;
import com.placenextai.dto.ReadinessResponse;
import com.placenextai.dto.ScoreConfigDto;
import com.placenextai.entity.Application;
import com.placenextai.entity.Job;
import com.placenextai.entity.RecruiterFeedback;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.RecruiterFeedbackRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.service.CandidateRankingService;
import com.placenextai.service.PlacementPredictionService;
import com.placenextai.service.ReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateRankingServiceImpl implements CandidateRankingService {

    private static final int NEUTRAL_INTERVIEW_SIGNAL = 60;

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;
    private final RecruiterFeedbackRepository feedbackRepository;
    private final ReadinessService readinessService;
    private final PlacementPredictionService placementPredictionService;

    @Override
    @Transactional(readOnly = true)
    public List<RankedCandidateResponse> rankCandidatesForJob(String recruiterEmail, Long jobId) {
        Job job = ownedJob(recruiterEmail, jobId);
        List<Application> applications = applicationRepository.findByJobOrderByAppliedDateDesc(job);

        ScoreConfigDto config = readinessService.getConfig();

        return applications.stream()
                .map(application -> score(application.getStudent(), job, config))
                .sorted(Comparator.comparingDouble(RankedCandidateResponse::getRankScore).reversed())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankedCandidateResponse> compareCandidates(String recruiterEmail, Long jobId, List<Long> studentIds) {
        Job job = ownedJob(recruiterEmail, jobId);
        ScoreConfigDto config = readinessService.getConfig();

        Set<Long> requested = new LinkedHashSet<>(studentIds);
        return applicationRepository.findByJobOrderByAppliedDateDesc(job).stream()
                .map(Application::getStudent)
                .filter(student -> requested.contains(student.getId()))
                .map(student -> score(student, job, config))
                .sorted(Comparator.comparingDouble(RankedCandidateResponse::getRankScore).reversed())
                .toList();
    }

    private Job ownedJob(String recruiterEmail, Long jobId) {
        Recruiter recruiter = recruiterRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with email: " + recruiterEmail));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        if (!recruiter.getCompanyName().equalsIgnoreCase(job.getCompany())) {
            throw new AccessDeniedException("You can only rank candidates for your own company's jobs");
        }
        return job;
    }

    private RankedCandidateResponse score(Student student, Job job, ScoreConfigDto config) {
        Set<String> studentSkills = splitSkills(student.getSkills());
        Set<String> requiredSkills = splitSkills(job.getSkillsRequired());

        List<String> matched = requiredSkills.stream()
                .filter(skill -> containsIgnoreCase(studentSkills, skill))
                .toList();
        List<String> missing = requiredSkills.stream()
                .filter(skill -> !containsIgnoreCase(studentSkills, skill))
                .toList();

        int skillMatchPercent = requiredSkills.isEmpty()
                ? 100
                : (int) Math.round(100.0 * matched.size() / requiredSkills.size());

        ReadinessResponse readiness = readinessService.getReadiness(student.getEmail());
        PlacementPredictionResponse prediction = placementPredictionService.getOrComputeLatest(student);
        int interviewSignal = averageInterviewRating(student.getId());

        double rankScore = config.getRankSkillWeight() * skillMatchPercent
                + config.getRankReadinessWeight() * readiness.getTotalScore()
                + config.getRankPredictionWeight() * prediction.getProbabilityScore()
                + config.getRankInterviewWeight() * interviewSignal;

        return RankedCandidateResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .email(student.getEmail())
                .jobId(job.getId())
                .jobTitle(job.getTitle())
                .rankScore(Math.round(rankScore * 10) / 10.0)
                .skillMatchPercent(skillMatchPercent)
                .readinessScore(readiness.getTotalScore())
                .predictionScore(prediction.getProbabilityScore())
                .interviewSignal(interviewSignal)
                .matchedSkills(matched)
                .missingSkills(missing)
                .build();
    }

    private int averageInterviewRating(Long studentId) {
        List<RecruiterFeedback> feedback = feedbackRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        if (feedback.isEmpty()) {
            return NEUTRAL_INTERVIEW_SIGNAL;
        }
        double average = feedback.stream()
                .mapToDouble(entry -> (entry.getCommunicationRating() + entry.getTechnicalRating()
                        + entry.getProblemSolvingRating() + entry.getCultureFitRating()) / 4.0)
                .average()
                .orElse(3.0);
        // Ratings are 1-5; map onto the same 0-100 scale as the other signals.
        return (int) Math.round(average / 5.0 * 100);
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
