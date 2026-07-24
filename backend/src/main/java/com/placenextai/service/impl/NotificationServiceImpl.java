package com.placenextai.service.impl;

import com.placenextai.dto.NotificationResponse;
import com.placenextai.entity.Notification;
import com.placenextai.entity.NotificationType;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.AdminRepository;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.NotificationRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final AdminRepository adminRepository;
    private final AlumniRepository alumniRepository;

    @Override
    @Transactional
    public void notify(Long recipientId, String recipientRole, NotificationType type, String title, String message, String link) {
        notificationRepository.save(Notification.builder()
                .recipientId(recipientId)
                .recipientRole(recipientRole)
                .type(type)
                .title(title)
                .message(message)
                .link(link)
                .read(false)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(String userEmail) {
        Identity identity = resolveIdentity(userEmail);
        return notificationRepository
                .findTop50ByRecipientIdAndRecipientRoleOrderByCreatedAtDesc(identity.id(), identity.role())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String userEmail) {
        Identity identity = resolveIdentity(userEmail);
        return notificationRepository.countByRecipientIdAndRecipientRoleAndReadFalse(identity.id(), identity.role());
    }

    @Override
    @Transactional
    public void markRead(String userEmail, Long id) {
        Identity identity = resolveIdentity(userEmail);
        Notification notification = notificationRepository
                .findByIdAndRecipientIdAndRecipientRole(id, identity.id(), identity.role())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllRead(String userEmail) {
        Identity identity = resolveIdentity(userEmail);
        List<Notification> unread = notificationRepository
                .findByRecipientIdAndRecipientRoleAndReadFalse(identity.id(), identity.role());
        unread.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private Identity resolveIdentity(String email) {
        var student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            return new Identity(student.get().getId(), "ROLE_STUDENT");
        }
        var recruiter = recruiterRepository.findByEmail(email);
        if (recruiter.isPresent()) {
            return new Identity(recruiter.get().getId(), "ROLE_RECRUITER");
        }
        var admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            return new Identity(admin.get().getId(), "ROLE_ADMIN");
        }
        var alumni = alumniRepository.findByEmail(email);
        if (alumni.isPresent()) {
            return new Identity(alumni.get().getId(), "ROLE_ALUMNI");
        }
        throw new ResourceNotFoundException("No account found with email: " + email);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .link(notification.getLink())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private record Identity(Long id, String role) {
    }
}
