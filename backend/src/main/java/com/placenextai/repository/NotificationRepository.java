package com.placenextai.repository;

import com.placenextai.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop50ByRecipientIdAndRecipientRoleOrderByCreatedAtDesc(Long recipientId, String recipientRole);

    long countByRecipientIdAndRecipientRoleAndReadFalse(Long recipientId, String recipientRole);

    Optional<Notification> findByIdAndRecipientIdAndRecipientRole(Long id, Long recipientId, String recipientRole);

    List<Notification> findByRecipientIdAndRecipientRoleAndReadFalse(Long recipientId, String recipientRole);
}
