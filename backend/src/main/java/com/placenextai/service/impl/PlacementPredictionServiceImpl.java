package com.placenextai.service.impl;

import com.placenextai.dto.PlacementPredictionResponse;
import com.placenextai.dto.PlacementPredictionResponse.Factor;
import com.placenextai.dto.PlacementPredictionResponse.HistoryPoint;
import com.placenextai.dto.ReadinessResponse;
import com.placenextai.entity.Application;
import com.placenextai.entity.ApplicationStatus;
import com.placenextai.entity.PlacementPrediction;
import com.placenextai.entity.RiskLevel;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.PlacementPredictionRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.PlacementPredictionService;
import com.placenextai.service.ReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlacementPredictionServiceImpl implements PlacementPredictionService {

    // Same progression ApplicationServiceImpl renders on the student timeline.
    // REJECTED is excluded deliberately - a rejected application contributes no
    // momentum, it isn't a step toward being hired.
    private static final List<ApplicationStatus> STAGE_ORDER = List.of(
            ApplicationStatus.APPLIED,
            ApplicationStatus.SHORTLISTED,
            ApplicationStatus.ASSESSMENT,
            ApplicationStatus.TECHNICAL_INTERVIEW,
            ApplicationStatus.HR_INTERVIEW,
            ApplicationStatus.OFFERED,
            ApplicationStatus.HIRED
    );

    private static final Map<String, String> DIMENSION_DESCRIPTIONS = Map.of(
            "Academic", "CGPA and core subject strength",
            "Resume", "Resume quality and ATS alignment",
            "Skills", "Breadth of listed, relevant skills",
            "Interview", "Mock interview performance",
            "Activity", "Recent platform activity and consistency"
    );

    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;
    private final PlacementPredictionRepository predictionRepository;
    private final ReadinessService readinessService;

    @Override
    @Transactional
    public PlacementPredictionResponse getPrediction(String studentEmail) {
        Student student = findStudent(studentEmail);
        PlacementPrediction latest = predictionRepository
                .findTopByStudentIdOrderByComputedAtDesc(student.getId())
                .orElseGet(() -> computeAndSave(student));
        return toResponse(latest, student);
    }

    @Override
    @Transactional
    public PlacementPredictionResponse recompute(String studentEmail) {
        Student student = findStudent(studentEmail);
        return toResponse(computeAndSave(student), student);
    }

    @Override
    @Transactional
    public PlacementPredictionResponse getOrComputeLatest(Student student) {
        PlacementPrediction latest = predictionRepository
                .findTopByStudentIdOrderByComputedAtDesc(student.getId())
                .orElseGet(() -> computeAndSave(student));
        return toResponse(latest, student);
    }

    private PlacementPrediction computeAndSave(Student student) {
        ReadinessResponse readiness = readinessService.getReadiness(student.getEmail());
        int momentum = applicationMomentum(student);

        int probability = clamp((int) Math.round(0.6 * readiness.getTotalScore() + 0.4 * momentum));
        RiskLevel risk = probability < 40 ? RiskLevel.HIGH : probability < 70 ? RiskLevel.MEDIUM : RiskLevel.LOW;

        return predictionRepository.save(PlacementPrediction.builder()
                .studentId(student.getId())
                .probabilityScore(probability)
                .riskLevel(risk)
                .build());
    }

    private int applicationMomentum(Student student) {
        List<Application> applications = applicationRepository.findByStudentOrderByAppliedDateDesc(student);
        int best = 0;
        for (Application application : applications) {
            if (application.getStatus() == ApplicationStatus.REJECTED) {
                continue;
            }
            int index = STAGE_ORDER.indexOf(application.getStatus());
            if (index < 0) {
                continue;
            }
            int percent = (int) Math.round(100.0 * (index + 1) / STAGE_ORDER.size());
            best = Math.max(best, percent);
        }
        return best;
    }

    private PlacementPredictionResponse toResponse(PlacementPrediction prediction, Student student) {
        ReadinessResponse readiness = readinessService.getReadiness(student.getEmail());
        int momentum = applicationMomentum(student);

        Map<String, Integer> dimensions = new LinkedHashMap<>();
        dimensions.put("Academic", readiness.getAcademicScore());
        dimensions.put("Resume", readiness.getResumeScore());
        dimensions.put("Skills", readiness.getSkillScore());
        dimensions.put("Interview", readiness.getInterviewScore());
        dimensions.put("Activity", readiness.getActivityScore());

        List<Factor> positives = new ArrayList<>();
        List<Factor> negatives = new ArrayList<>();

        dimensions.forEach((label, value) -> {
            if (value >= 65) {
                positives.add(Factor.builder()
                        .label(label)
                        .impact(value)
                        .description(DIMENSION_DESCRIPTIONS.get(label) + " is a strength.")
                        .build());
            } else if (value < 40) {
                negatives.add(Factor.builder()
                        .label(label)
                        .impact(value)
                        .description(DIMENSION_DESCRIPTIONS.get(label) + " is currently holding your score back.")
                        .build());
            }
        });

        if (momentum > 0) {
            positives.add(Factor.builder()
                    .label("Application Momentum")
                    .impact(momentum)
                    .description("Your furthest application has progressed " + momentum + "% through the hiring pipeline.")
                    .build());
        } else {
            negatives.add(Factor.builder()
                    .label("Application Momentum")
                    .impact(0)
                    .description("You have no active applications yet - momentum is the fastest way to move this score.")
                    .build());
        }

        if (readiness.getFeedbackAdjustment() > 0) {
            positives.add(Factor.builder()
                    .label("Recruiter Feedback")
                    .impact(readiness.getFeedbackAdjustment())
                    .description("Recent recruiter feedback has been positive.")
                    .build());
        } else if (readiness.getFeedbackAdjustment() < 0) {
            negatives.add(Factor.builder()
                    .label("Recruiter Feedback")
                    .impact(readiness.getFeedbackAdjustment())
                    .description("Recent recruiter feedback has been below average.")
                    .build());
        }

        List<String> recommendations = new ArrayList<>();
        if (readiness.getImprovementTip() != null) {
            recommendations.add(readiness.getImprovementTip());
        }
        if (momentum == 0) {
            recommendations.add("Apply to a job matching your skills to start building application momentum.");
        }

        List<HistoryPoint> history = predictionRepository
                .findTop20ByStudentIdOrderByComputedAtDesc(student.getId()).stream()
                .map(point -> HistoryPoint.builder()
                        .probabilityScore(point.getProbabilityScore())
                        .computedAt(point.getComputedAt())
                        .build())
                .toList()
                .reversed();

        return PlacementPredictionResponse.builder()
                .probabilityScore(prediction.getProbabilityScore())
                .riskLevel(prediction.getRiskLevel().name())
                .positiveFactors(positives)
                .negativeFactors(negatives)
                .recommendations(recommendations)
                .computedAt(prediction.getComputedAt())
                .history(history)
                .build();
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private Student findStudent(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }
}
