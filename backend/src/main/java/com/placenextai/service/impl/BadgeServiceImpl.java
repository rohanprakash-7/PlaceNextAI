package com.placenextai.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.placenextai.dto.BadgeResponse;
import com.placenextai.entity.Application;
import com.placenextai.entity.ApplicationStatus;
import com.placenextai.entity.Badge;
import com.placenextai.entity.EventType;
import com.placenextai.entity.NotificationType;
import com.placenextai.entity.Roadmap;
import com.placenextai.entity.Student;
import com.placenextai.entity.StudentBadge;
import com.placenextai.exception.AiServiceException;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.BadgeRepository;
import com.placenextai.repository.PlatformEventRepository;
import com.placenextai.repository.RoadmapItemRepository;
import com.placenextai.repository.RoadmapRepository;
import com.placenextai.repository.StudentBadgeRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.BadgeService;
import com.placenextai.service.EmailService;
import com.placenextai.service.GamificationService;
import com.placenextai.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final StudentBadgeRepository studentBadgeRepository;
    private final PlatformEventRepository eventRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapItemRepository roadmapItemRepository;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;
    private final EmailService emailService;

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
                if (hasCompletedRoadmap(studentId)) {
                    award(studentId, SKILL_MASTER);
                }
            }
            case MOCK_INTERVIEW_COMPLETED -> {
                if (eventRepository.countByStudentIdAndEventType(studentId, EventType.MOCK_INTERVIEW_COMPLETED) >= 1) {
                    award(studentId, INTERVIEW_READY);
                }
                Student student = studentRepository.findById(studentId).orElse(null);
                if (student != null && student.getMockInterviewScore() != null && student.getMockInterviewScore() >= 85) {
                    award(studentId, INTERVIEW_ACE);
                }
            }
            case APPLICATION_STATUS_CHANGED -> {
                Student student = studentRepository.findById(studentId).orElse(null);
                if (student == null) {
                    return;
                }
                List<ApplicationStatus> statuses = applicationRepository.findByStudentOrderByAppliedDateDesc(student)
                        .stream()
                        .map(Application::getStatus)
                        .toList();
                if (statuses.stream().anyMatch(status -> status == ApplicationStatus.OFFERED || status == ApplicationStatus.HIRED)) {
                    award(studentId, OFFER_RECEIVED);
                }
                if (statuses.stream().anyMatch(status -> status == ApplicationStatus.HIRED)) {
                    award(studentId, PLACED);
                }
            }
            case MENTOR_REQUEST_ACCEPTED -> award(studentId, MENTORSHIP_STARTER);
            default -> {
                // No badge is tied to this event type.
            }
        }
    }

    @Override
    @Transactional
    public void checkStreak(Long studentId, int currentStreak) {
        if (currentStreak >= 7) {
            award(studentId, STREAK_7);
        }
        if (currentStreak >= 30) {
            award(studentId, STREAK_30);
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
                .filter(badge -> !"RECRUITER".equals(badge.getCategory()))
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

    @Override
    @Transactional(readOnly = true)
    public byte[] generateCertificate(String studentEmail, String badgeCode) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));
        Badge badge = badgeRepository.findByCode(badgeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found: " + badgeCode));

        StudentBadge earned = studentBadgeRepository.findByStudentId(student.getId()).stream()
                .filter(studentBadge -> studentBadge.getBadgeId().equals(badge.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("You have not earned this badge yet"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 50, 50, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 30, Font.BOLD);
            Font subFont = new Font(Font.HELVETICA, 14, Font.NORMAL);
            Font nameFont = new Font(Font.HELVETICA, 24, Font.BOLDITALIC);
            Font badgeFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font footerFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

            Paragraph brand = new Paragraph("PlaceNextAI", subFont);
            brand.setAlignment(Element.ALIGN_CENTER);
            document.add(brand);

            Paragraph spacer = new Paragraph(" ");
            document.add(spacer);

            Paragraph title = new Paragraph("Certificate of Achievement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            Paragraph presented = new Paragraph("This certifies that", subFont);
            presented.setAlignment(Element.ALIGN_CENTER);
            document.add(presented);

            Paragraph name = new Paragraph(student.getFullName(), nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);
            document.add(new Paragraph(" "));

            Paragraph earnedText = new Paragraph("has earned the badge", subFont);
            earnedText.setAlignment(Element.ALIGN_CENTER);
            document.add(earnedText);

            Paragraph badgeName = new Paragraph(badge.getName(), badgeFont);
            badgeName.setAlignment(Element.ALIGN_CENTER);
            document.add(badgeName);

            Paragraph description = new Paragraph(badge.getDescription(), subFont);
            description.setAlignment(Element.ALIGN_CENTER);
            document.add(description);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            Paragraph date = new Paragraph("Awarded on " + earned.getAwardedAt().toLocalDate(), footerFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);

            document.close();
            return out.toByteArray();
        } catch (Exception exception) {
            throw new AiServiceException("Could not generate the certificate: " + exception.getMessage());
        }
    }

    private boolean hasCompletedRoadmap(Long studentId) {
        List<Roadmap> roadmaps = roadmapRepository.findByStudentIdAndStatus(studentId, com.placenextai.entity.RoadmapStatus.ACTIVE);
        for (Roadmap roadmap : roadmaps) {
            long total = roadmapItemRepository.countByRoadmapId(roadmap.getId());
            long done = roadmapItemRepository.countByRoadmapIdAndCompletedTrue(roadmap.getId());
            if (total > 0 && total == done) {
                return true;
            }
        }
        return false;
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
        gamificationService.addXp(studentId, badge.getXpReward());

        String message = "You earned the \"" + badge.getName() + "\" badge - " + badge.getDescription();
        notificationService.notify(studentId, "ROLE_STUDENT", NotificationType.BADGE_EARNED,
                "New badge earned!", message, "/dashboard/student/achievements");
        studentRepository.findById(studentId).ifPresent(student ->
                emailService.send(student.getEmail(), "New badge earned!", message));
    }
}
