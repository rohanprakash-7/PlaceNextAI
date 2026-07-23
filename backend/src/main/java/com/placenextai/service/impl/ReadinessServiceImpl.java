package com.placenextai.service.impl;

import com.placenextai.dto.ReadinessResponse;
import com.placenextai.dto.ScoreConfigDto;
import com.placenextai.entity.ReadinessScore;
import com.placenextai.entity.ScoreConfig;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.entity.RecruiterFeedback;
import com.placenextai.repository.PlatformEventRepository;
import com.placenextai.repository.RecruiterFeedbackRepository;
import com.placenextai.repository.ReadinessScoreRepository;
import com.placenextai.repository.ScoreConfigRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.ReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReadinessServiceImpl implements ReadinessService {

    private final StudentRepository studentRepository;
    private final ReadinessScoreRepository scoreRepository;
    private final ScoreConfigRepository configRepository;
    private final PlatformEventRepository eventRepository;
    private final RecruiterFeedbackRepository feedbackRepository;

    private static final Map<String, String> TIPS = Map.of(
            "Academic", "Focus on your CGPA and core subjects - academic strength anchors your score.",
            "Resume", "Upload or improve your resume - it is currently your weakest dimension.",
            "Skills", "Add more relevant skills to your profile and back them with projects.",
            "Interview", "Complete mock interviews to lift your interview readiness.",
            "Activity", "Be more active - regular practice and applications raise your consistency score."
    );

    @Override
    @Transactional(readOnly = true)
    public ReadinessResponse getReadiness(String studentEmail) {
        Student student = findStudent(studentEmail);
        ReadinessScore latest = scoreRepository
                .findTopByStudentIdOrderByComputedAtDesc(student.getId())
                .orElseGet(() -> computeAndSave(student, "INITIAL"));
        return toResponse(latest, student.getId());
    }

    @Override
    @Transactional
    public ReadinessResponse recompute(String studentEmail, String triggeredBy) {
        Student student = findStudent(studentEmail);
        return toResponse(computeAndSave(student, triggeredBy), student.getId());
    }

    @Override
    @Transactional
    public void recomputeForStudentId(Long studentId, String triggeredBy) {
        studentRepository.findById(studentId)
                .ifPresent(student -> computeAndSave(student, triggeredBy));
    }

    @Override
    @Transactional(readOnly = true)
    public ScoreConfigDto getConfig() {
        return toDto(currentConfig());
    }

    @Override
    @Transactional
    public ScoreConfigDto updateConfig(ScoreConfigDto request) {
        double sum = request.getAcademicWeight() + request.getResumeWeight()
                + request.getSkillWeight() + request.getInterviewWeight()
                + request.getActivityWeight();
        if (Math.abs(sum - 1.0) > 0.001) {
            throw new IllegalArgumentException("Weights must sum to 1.0 (currently " + sum + ")");
        }
        double rankSum = request.getRankSkillWeight() + request.getRankReadinessWeight()
                + request.getRankPredictionWeight() + request.getRankInterviewWeight();
        if (Math.abs(rankSum - 1.0) > 0.001) {
            throw new IllegalArgumentException("Ranking weights must sum to 1.0 (currently " + rankSum + ")");
        }
        ScoreConfig config = currentConfig();
        config.setAcademicWeight(request.getAcademicWeight());
        config.setResumeWeight(request.getResumeWeight());
        config.setSkillWeight(request.getSkillWeight());
        config.setInterviewWeight(request.getInterviewWeight());
        config.setActivityWeight(request.getActivityWeight());
        config.setFeedbackAdjustmentCap(request.getFeedbackAdjustmentCap());
        config.setRankSkillWeight(request.getRankSkillWeight());
        config.setRankReadinessWeight(request.getRankReadinessWeight());
        config.setRankPredictionWeight(request.getRankPredictionWeight());
        config.setRankInterviewWeight(request.getRankInterviewWeight());
        return toDto(configRepository.save(config));
    }

    // ---------------- score engine ----------------

    private ReadinessScore computeAndSave(Student student, String triggeredBy) {
        ScoreConfig config = currentConfig();

        int academic = academicScore(student);
        int resume = resumeScore(student);
        int skills = skillScore(student);
        int interview = interviewScore(student);
        int activity = activityScore(student);
        int feedbackAdjustment = feedbackAdjustment(student, config);

        double weighted =
                academic * config.getAcademicWeight()
                        + resume * config.getResumeWeight()
                        + skills * config.getSkillWeight()
                        + interview * config.getInterviewWeight()
                        + activity * config.getActivityWeight()
                        + feedbackAdjustment;

        int total = Math.max(0, Math.min(100, (int) Math.round(weighted)));

        return scoreRepository.save(ReadinessScore.builder()
                .studentId(student.getId())
                .totalScore(total)
                .academicScore(academic)
                .resumeScore(resume)
                .skillScore(skills)
                .interviewScore(interview)
                .activityScore(activity)
                .feedbackAdjustment(feedbackAdjustment)
                .triggeredBy(triggeredBy)
                .build());
    }

    private int academicScore(Student student) {
        if (student.getCgpa() == null) return 0;
        return clamp((int) Math.round(student.getCgpa() * 10));
    }

    private int resumeScore(Student student) {
        if (student.getResumeScore() != null) return clamp(student.getResumeScore());
        boolean uploaded = student.getResumeUrl() != null && !student.getResumeUrl().isBlank();
        return uploaded ? 50 : 0;
    }

    private int skillScore(Student student) {
        if (student.getSkills() == null || student.getSkills().isBlank()) return 0;
        long count = java.util.Arrays.stream(student.getSkills().split(","))
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .distinct()
                .count();
        return clamp((int) (count * 10));
    }

    private int interviewScore(Student student) {
        return student.getMockInterviewScore() == null ? 0 : clamp(student.getMockInterviewScore());
    }

    private int activityScore(Student student) {
        long recentEvents = eventRepository.countByStudentIdAndCreatedAtAfter(
                student.getId(), LocalDateTime.now().minusDays(30));
        return clamp((int) (recentEvents * 5));
    }

    /**
     * Turns recruiter feedback into a bounded score nudge: average rating 3/5
     * (neutral) contributes nothing, 5/5 adds up to the configured cap, 1/5
     * subtracts up to the cap. This is the step that closes the loop -
     * what a recruiter reports back changes the student's own score.
     */
    private int feedbackAdjustment(Student student, ScoreConfig config) {
        List<RecruiterFeedback> feedback = feedbackRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
        if (feedback.isEmpty()) {
            return 0;
        }
        double averageRating = feedback.stream()
                .mapToDouble(entry -> (entry.getCommunicationRating() + entry.getTechnicalRating()
                        + entry.getProblemSolvingRating() + entry.getCultureFitRating()) / 4.0)
                .average()
                .orElse(3.0);

        int cap = config.getFeedbackAdjustmentCap();
        int adjustment = (int) Math.round((averageRating - 3.0) / 2.0 * cap);
        return Math.max(-cap, Math.min(cap, adjustment));
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    // ---------------- helpers ----------------

    private ReadinessResponse toResponse(ReadinessScore score, Long studentId) {
        Map<String, Integer> dimensions = new LinkedHashMap<>();
        dimensions.put("Academic", score.getAcademicScore());
        dimensions.put("Resume", score.getResumeScore());
        dimensions.put("Skills", score.getSkillScore());
        dimensions.put("Interview", score.getInterviewScore());
        dimensions.put("Activity", score.getActivityScore());

        String weakest = dimensions.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Resume");

        List<ReadinessResponse.ScoreHistoryPoint> history = scoreRepository
                .findTop20ByStudentIdOrderByComputedAtDesc(studentId).stream()
                .map(point -> ReadinessResponse.ScoreHistoryPoint.builder()
                        .totalScore(point.getTotalScore())
                        .computedAt(point.getComputedAt())
                        .build())
                .toList()
                .reversed();

        return ReadinessResponse.builder()
                .totalScore(score.getTotalScore())
                .academicScore(score.getAcademicScore())
                .resumeScore(score.getResumeScore())
                .skillScore(score.getSkillScore())
                .interviewScore(score.getInterviewScore())
                .activityScore(score.getActivityScore())
                .feedbackAdjustment(score.getFeedbackAdjustment())
                .weakestDimension(weakest)
                .improvementTip(TIPS.get(weakest))
                .computedAt(score.getComputedAt())
                .history(history)
                .build();
    }

    private ScoreConfig currentConfig() {
        ScoreConfig config = configRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> configRepository.save(defaultConfig()));

        // A config row created before the Phase 6.2 ranking columns existed
        // gets those columns added as 0 by the schema update, not backfilled
        // with the code-level defaults below - fix that up the first time
        // such a row is read, rather than leaving ranking permanently zeroed.
        double rankWeightSum = config.getRankSkillWeight() + config.getRankReadinessWeight()
                + config.getRankPredictionWeight() + config.getRankInterviewWeight();
        if (rankWeightSum == 0) {
            config.setRankSkillWeight(0.35);
            config.setRankReadinessWeight(0.25);
            config.setRankPredictionWeight(0.25);
            config.setRankInterviewWeight(0.15);
            config = configRepository.save(config);
        }

        return config;
    }

    private ScoreConfig defaultConfig() {
        return ScoreConfig.builder()
                .academicWeight(0.25)
                .resumeWeight(0.20)
                .skillWeight(0.20)
                .interviewWeight(0.20)
                .activityWeight(0.15)
                .feedbackAdjustmentCap(10)
                .rankSkillWeight(0.35)
                .rankReadinessWeight(0.25)
                .rankPredictionWeight(0.25)
                .rankInterviewWeight(0.15)
                .build();
    }

    private ScoreConfigDto toDto(ScoreConfig config) {
        return ScoreConfigDto.builder()
                .academicWeight(config.getAcademicWeight())
                .resumeWeight(config.getResumeWeight())
                .skillWeight(config.getSkillWeight())
                .interviewWeight(config.getInterviewWeight())
                .activityWeight(config.getActivityWeight())
                .feedbackAdjustmentCap(config.getFeedbackAdjustmentCap())
                .rankSkillWeight(config.getRankSkillWeight())
                .rankReadinessWeight(config.getRankReadinessWeight())
                .rankPredictionWeight(config.getRankPredictionWeight())
                .rankInterviewWeight(config.getRankInterviewWeight())
                .build();
    }

    private Student findStudent(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }
}
