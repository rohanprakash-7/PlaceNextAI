package com.placenextai.service;

import com.placenextai.dto.NotificationResponse;
import com.placenextai.entity.NotificationType;

import java.util.List;

public interface NotificationService {

    void notify(Long recipientId, String recipientRole, NotificationType type, String title, String message, String link);

    List<NotificationResponse> getMyNotifications(String userEmail);

    long getUnreadCount(String userEmail);

    void markRead(String userEmail, Long id);

    void markAllRead(String userEmail);
}
