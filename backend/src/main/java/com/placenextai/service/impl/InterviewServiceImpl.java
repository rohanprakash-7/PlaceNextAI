package com.placenextai.service.impl;

import com.placenextai.dto.InterviewQuestionResponse;
import com.placenextai.dto.InterviewSessionResponse;
import com.placenextai.dto.StartInterviewRequest;
import com.placenextai.dto.SubmitAnswerRequest;
import com.placenextai.entity.EventType;
import com.placenextai.entity.InterviewQuestion;
import com.placenextai.entity.InterviewSession;
import com.placenextai.entity.InterviewSessionStatus;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.InterviewQuestionRepository;
import com.placenextai.repository.InterviewSessionRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EventService;
import com.placenextai.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final InterviewQuestionBank questionBank;
    private final EventService eventService;

    @Override
    @Transactional
    public InterviewSessionResponse startInterview(String studentEmail, StartInterviewRequest request) {
        Student student = findStudent(studentEmail);
        String targetCompany = request.getTargetCompany() == null || request.getTargetCompany().isBlank()
                ? null
                : request.getTargetCompany().trim();

        Set<String> studentSkills = splitSkills(student.getSkills());
        Set<String> companyRequiredSkills = targetCompany == null
                ? Set.of()
                : jobRepository.findByCompanyIgnoreCaseOrderByCreatedDateDesc(targetCompany).stream()
                        .flatMap(job -> splitSkills(job.getSkillsRequired()).stream())
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        InterviewSession session = interviewSessionRepository.save(InterviewSession.builder()
                .studentId(student.getId())
                .targetCompany(targetCompany)
                .status(InterviewSessionStatus.ACTIVE)
                .build());

        List<InterviewQuestionBank.GeneratedQuestion> generated =
                questionBank.generate(studentSkills, targetCompany, companyRequiredSkills);

        int order = 1;
        for (InterviewQuestionBank.GeneratedQuestion question : generated) {
            interviewQuestionRepository.save(InterviewQuestion.builder()
                    .sessionId(session.getId())
                    .questionOrder(order++)
                    .category(question.category())
                    .questionText(question.text())
                    .expectedKeywords(String.join(",", question.keywords()))
                    .build());
        }

        return toSessionResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewSessionResponse getSession(String studentEmail, Long sessionId) {
        Student student = findStudent(studentEmail);
        InterviewSession session = findSession(sessionId, student.getId());
        return toSessionResponse(session);
    }

    @Override
    @Transactional
    public InterviewQuestionResponse submitAnswer(String studentEmail, Long sessionId, Long questionId, SubmitAnswerRequest request) {
        Student student = findStudent(studentEmail);
        InterviewSession session = findSession(sessionId, student.getId());

        if (session.getStatus() != InterviewSessionStatus.ACTIVE) {
            throw new IllegalArgumentException("This interview has already been completed");
        }

        InterviewQuestion question = interviewQuestionRepository.findByIdAndSessionId(questionId, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionId));

        List<String> expectedKeywords = question.getExpectedKeywords() == null || question.getExpectedKeywords().isBlank()
                ? List.of()
                : Arrays.asList(question.getExpectedKeywords().split(","));

        String answer = request.getAnswerText().trim();
        int wordCount = answer.isEmpty() ? 0 : answer.split("\\s+").length;

        List<String> hitKeywords = expectedKeywords.stream()
                .filter(keyword -> answer.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)))
                .toList();
        List<String> missedKeywords = expectedKeywords.stream()
                .filter(keyword -> !hitKeywords.contains(keyword))
                .toList();

        int lengthScore = Math.min(40, wordCount * 2);
        int keywordScore = expectedKeywords.isEmpty()
                ? 40
                : (int) Math.min(60, Math.round(60.0 * hitKeywords.size() / expectedKeywords.size()));
        int score = Math.max(0, Math.min(100, lengthScore + keywordScore));

        String feedback = buildFeedback(score, missedKeywords);

        question.setStudentAnswer(answer);
        question.setScore(score);
        question.setFeedback(feedback);
        question.setAnsweredAt(java.time.LocalDateTime.now());
        interviewQuestionRepository.save(question);

        return toQuestionResponse(question);
    }

    @Override
    @Transactional
    public InterviewSessionResponse completeInterview(String studentEmail, Long sessionId) {
        Student student = findStudent(studentEmail);
        InterviewSession session = findSession(sessionId, student.getId());

        if (session.getStatus() == InterviewSessionStatus.COMPLETED) {
            return toSessionResponse(session);
        }

        List<InterviewQuestion> questions = interviewQuestionRepository.findBySessionIdOrderByQuestionOrderAsc(sessionId);
        long unanswered = questions.stream().filter(question -> question.getScore() == null).count();
        if (unanswered > 0) {
            throw new IllegalArgumentException("Please answer all questions before finishing the interview");
        }

        int overallScore = (int) Math.round(
                questions.stream().mapToInt(InterviewQuestion::getScore).average().orElse(0));

        session.setStatus(InterviewSessionStatus.COMPLETED);
        session.setOverallScore(overallScore);
        session.setCompletedAt(java.time.LocalDateTime.now());
        interviewSessionRepository.save(session);

        student.setMockInterviewScore(overallScore);
        studentRepository.save(student);

        eventService.record(student.getId(), EventType.MOCK_INTERVIEW_COMPLETED,
                "Completed mock interview" + (session.getTargetCompany() != null ? " for " + session.getTargetCompany() : "")
                        + " - score " + overallScore);

        return toSessionResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewSessionResponse> getHistory(String studentEmail) {
        Student student = findStudent(studentEmail);
        return interviewSessionRepository.findTop10ByStudentIdOrderByStartedAtDesc(student.getId()).stream()
                .map(this::toSessionResponse)
                .toList();
    }

    private String buildFeedback(int score, List<String> missedKeywords) {
        String missedHint = missedKeywords.isEmpty() ? "" : " Try touching on: " + String.join(", ", missedKeywords) + ".";
        if (score >= 80) {
            return "Strong answer - well-structured and covers the key points.";
        }
        if (score >= 60) {
            return "Good answer." + missedHint;
        }
        if (score >= 40) {
            return "Decent start, but add more specific detail." + missedHint;
        }
        return "This answer needs more depth - back it with a concrete example." + missedHint;
    }

    private InterviewSessionResponse toSessionResponse(InterviewSession session) {
        List<InterviewQuestionResponse> questions = interviewQuestionRepository
                .findBySessionIdOrderByQuestionOrderAsc(session.getId()).stream()
                .map(this::toQuestionResponse)
                .toList();

        return InterviewSessionResponse.builder()
                .id(session.getId())
                .targetCompany(session.getTargetCompany())
                .status(session.getStatus().name())
                .overallScore(session.getOverallScore())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .questions(questions)
                .build();
    }

    private InterviewQuestionResponse toQuestionResponse(InterviewQuestion question) {
        return InterviewQuestionResponse.builder()
                .id(question.getId())
                .questionOrder(question.getQuestionOrder())
                .category(question.getCategory().name())
                .questionText(question.getQuestionText())
                .answered(question.getScore() != null)
                .studentAnswer(question.getStudentAnswer())
                .score(question.getScore())
                .feedback(question.getFeedback())
                .build();
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

    private Student findStudent(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }

    private InterviewSession findSession(Long sessionId, Long studentId) {
        return interviewSessionRepository.findByIdAndStudentId(sessionId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview session not found: " + sessionId));
    }
}
