package com.placenextai.service.impl;

import com.placenextai.dto.BadgeResponse;
import com.placenextai.entity.Application;
import com.placenextai.entity.ApplicationStatus;
import com.placenextai.entity.Badge;
import com.placenextai.entity.EventType;
import com.placenextai.entity.Student;
import com.placenextai.entity.StudentBadge;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.BadgeRepository;
import com.placenextai.repository.PlatformEventRepository;
import com.placenextai.repository.StudentBadgeRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final StudentBadgeRepository studentBadgeRepository;
    private final PlatformEventRepository eventRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void checkAndAward(Long studentId, EventType triggeringType) {
        switch (triggeringType) {
            case APPLICATION_SUBMITTED -> {
                if (eventRepository.countByStudentIdAndEventType(studentId, EventType.APPLICATION_SUBMITTED) >= 1) {
                    award(studentId, FIRST_APPLICATION);
                }
            }
            case RESUME_UPLOADED -> {
                if (eventRepository.countByStudentIdAndEventType(studentId, EventType.RESUME_UPLOADED) >= 3) {
                    award(studentId, RESUME_REFINED);
                }
            }
            case ROADMAP_ITEM_COMPLETED -> {
                long recent = eventRepository.countByStudentIdAndEventTypeAndCreatedAtAfter(
                        studentId, EventType.ROADMAP_ITEM_COMPLETED, LocalDateTime.now().minusDays(28));
                if (recent >= 4) {
                    award(studentId, CONSISTENCY_STREAK);
                }
            }
            case MOCK_INTERVIEW_COMPLETED -> {
                if (eventRepository.countByStudentIdAndEventType(studentId, EventType.MOCK_INTERVIEW_COMPLETED) >= 1) {
                    award(studentId, INTERVIEW_READY);
                }
            }
            case APPLICATION_STATUS_CHANGED -> {
                Student student = studentRepository.findById(studentId).orElse(null);
                if (student == null) {
                    return;
                }
                boolean reachedOffer = applicationRepository.findByStudentOrderByAppliedDateDesc(student).stream()
                        .map(Application::getStatus)
                        .anyMatch(status -> status == ApplicationStatus.OFFERED || status == ApplicationStatus.HIRED);
                if (reachedOffer) {
                    award(studentId, OFFER_RECEIVED);
                }
            }
            default -> {
                // No badge is tied to this event type.
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeResponse> getBadgesForStudent(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));

        Map<Long, LocalDateTime> earnedByBadgeId = studentBadgeRepository.findByStudentId(student.getId()).stream()
                .collect(Collectors.toMap(StudentBadge::getBadgeId, StudentBadge::getAwardedAt));

        return badgeRepository.findAll().stream()
                .map(badge -> BadgeResponse.builder()
                        .code(badge.getCode())
                        .name(badge.getName())
                        .description(badge.getDescription())
                        .icon(badge.getIcon())
                        .earned(earnedByBadgeId.containsKey(badge.getId()))
                        .awardedAt(earnedByBadgeId.get(badge.getId()))
                        .build())
                .toList();
    }

    private void award(Long studentId, String code) {
        Badge badge = badgeRepository.findByCode(code).orElse(null);
        if (badge == null) {
            return;
        }
        if (studentBadgeRepository.existsByStudentIdAndBadgeId(studentId, badge.getId())) {
            return;
        }
        studentBadgeRepository.save(StudentBadge.builder()
                .studentId(studentId)
                .badgeId(badge.getId())
                .build());
    }
}
