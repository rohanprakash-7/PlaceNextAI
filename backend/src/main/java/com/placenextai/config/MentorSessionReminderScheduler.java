package com.placenextai.config;

import com.placenextai.entity.Alumni;
import com.placenextai.entity.MentorSlot;
import com.placenextai.entity.NotificationType;
import com.placenextai.entity.Student;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.MentorSlotRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EmailService;
import com.placenextai.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Reminds both sides of a booked mentor session shortly before it starts.
 * Runs every 5 minutes and looks 55 minutes ahead so each slot gets exactly
 * one reminder, roughly an hour before it begins.
 */
@Component
@RequiredArgsConstructor
public class MentorSessionReminderScheduler {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM, h:mm a");

    private final MentorSlotRepository mentorSlotRepository;
    private final StudentRepository studentRepository;
    private final AlumniRepository alumniRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Scheduled(fixedRate = 300_000)
    @Transactional
    public void sendUpcomingSessionReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<MentorSlot> upcoming = mentorSlotRepository
                .findByBookedTrueAndReminderSentFalseAndStartTimeBetween(now, now.plusMinutes(65));

        for (MentorSlot slot : upcoming) {
            Student student = studentRepository.findById(slot.getStudentId()).orElse(null);
            Alumni alumni = alumniRepository.findById(slot.getAlumniId()).orElse(null);
            if (student == null || alumni == null) {
                continue;
            }

            String when = slot.getStartTime().format(DISPLAY_FORMAT);

            String studentMessage = "Your mentor session with " + alumni.getFullName() + " starts at " + when;
            notificationService.notify(student.getId(), "ROLE_STUDENT", NotificationType.SESSION_REMINDER,
                    "Mentor session starting soon", studentMessage, "/dashboard/student/mentors");
            emailService.send(student.getEmail(), "Mentor session starting soon", studentMessage);

            String alumniMessage = "Your mentor session with " + student.getFullName() + " starts at " + when;
            notificationService.notify(alumni.getId(), "ROLE_ALUMNI", NotificationType.SESSION_REMINDER,
                    "Mentor session starting soon", alumniMessage, "/dashboard/alumni/slots");
            emailService.send(alumni.getEmail(), "Mentor session starting soon", alumniMessage);

            slot.setReminderSent(true);
            mentorSlotRepository.save(slot);
        }
    }
}
