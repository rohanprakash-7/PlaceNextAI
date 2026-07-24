package com.placenextai.service.impl;

import com.placenextai.dto.DayCountResponse;
import com.placenextai.dto.EventResponse;
import com.placenextai.entity.EventType;
import com.placenextai.entity.NotificationType;
import com.placenextai.entity.PlatformEvent;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.PlatformEventRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.BadgeService;
import com.placenextai.service.EmailService;
import com.placenextai.service.EventService;
import com.placenextai.service.GamificationService;
import com.placenextai.service.NotificationService;
import com.placenextai.service.ReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    // Only student-facing events worth interrupting the student for get a notification -
    // logins/profile edits etc would just be noise.
    private static final Map<EventType, String> NOTIFICATION_TITLES = new EnumMap<>(EventType.class);

    static {
        NOTIFICATION_TITLES.put(EventType.APPLICATION_STATUS_CHANGED, "Application update");
        NOTIFICATION_TITLES.put(EventType.MENTOR_REQUEST_ACCEPTED, "Mentorship request accepted");
        NOTIFICATION_TITLES.put(EventType.MENTOR_REQUEST_REJECTED, "Mentorship request declined");
        NOTIFICATION_TITLES.put(EventType.MENTOR_SESSION_BOOKED, "Mentor session confirmed");
    }

    private final PlatformEventRepository eventRepository;
    private final StudentRepository studentRepository;
    private final ReadinessService readinessService;
    private final BadgeService badgeService;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    @Transactional
    public void record(Long studentId, EventType type, String payload) {
        eventRepository.save(PlatformEvent.builder()
                .studentId(studentId)
                .eventType(type)
                .payload(payload)
                .build());
        // Every event re-scores the student: this is the platform's core loop.
        readinessService.recomputeForStudentId(studentId, type.name());
        int currentStreak = gamificationService.recordActivity(studentId, type);
        badgeService.checkAndAward(studentId, type);
        badgeService.checkStreak(studentId, currentStreak);
        notifyIfNoteworthy(studentId, type, payload);
    }

    private void notifyIfNoteworthy(Long studentId, EventType type, String payload) {
        String title = NOTIFICATION_TITLES.get(type);
        if (title == null) {
            return;
        }
        notificationService.notify(studentId, "ROLE_STUDENT", NotificationType.valueOf(mapToNotificationType(type)),
                title, payload, linkFor(type));

        studentRepository.findById(studentId).ifPresent(student ->
                emailService.send(student.getEmail(), title, payload));
    }

    private String linkFor(EventType type) {
        return switch (type) {
            case APPLICATION_STATUS_CHANGED -> "/dashboard/student/applications";
            case MENTOR_REQUEST_ACCEPTED, MENTOR_REQUEST_REJECTED -> "/dashboard/student/mentors/requests";
            case MENTOR_SESSION_BOOKED -> "/dashboard/student/mentors";
            default -> null;
        };
    }

    private String mapToNotificationType(EventType type) {
        return switch (type) {
            case APPLICATION_STATUS_CHANGED -> "APPLICATION_STATUS";
            case MENTOR_REQUEST_ACCEPTED, MENTOR_REQUEST_REJECTED -> "MENTOR_REQUEST";
            case MENTOR_SESSION_BOOKED -> "MENTOR_SESSION";
            default -> "SYSTEM";
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> recentEvents(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        return eventRepository.findTop20ByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(event -> EventResponse.builder()
                        .id(event.getId())
                        .eventType(event.getEventType().name())
                        .payload(event.getPayload())
                        .createdAt(event.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayCountResponse> activityHeatmap(String studentEmail, int days) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));

        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return eventRepository.countByDaySince(student.getId(), since).stream()
                .map(row -> DayCountResponse.builder()
                        .date(toLocalDate(row[0]))
                        .count(((Number) row[1]).longValue())
                        .build())
                .toList();
    }

    private LocalDate toLocalDate(Object rawDate) {
        if (rawDate instanceof Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return (LocalDate) rawDate;
    }
}
