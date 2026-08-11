package com.manada.backend.modules.notifications;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.common.security.AuthenticatedUser;
import com.manada.backend.modules.notifications.dto.NotificationResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public List<NotificationResponse> listMine(@AuthenticationPrincipal AuthenticatedUser user) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.id()).stream()
            .map(NotificationResponse::from).toList();
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        Notification n = notificationRepository.findById(id)
            .orElseThrow(() -> ApiException.notFound("Notificación no encontrada."));
        if (!n.getUserId().equals(user.id())) {
            throw ApiException.forbidden("No es tu notificación.");
        }
        n.setRead(true);
        return NotificationResponse.from(notificationRepository.save(n));
    }
}
