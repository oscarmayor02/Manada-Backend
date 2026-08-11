package com.manada.backend.modules.notifications.dto;

import com.manada.backend.modules.notifications.Notification;
import com.manada.backend.modules.notifications.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, NotificationType type, String title, String body, boolean read, Instant createdAt) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getType(), n.getTitle(), n.getBody(), n.isRead(), n.getCreatedAt());
    }
}
