package com.manada.backend.modules.notifications;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Envía notificaciones a los usuarios. Hoy solo guarda un registro en la
 * base de datos (para la campanita de la app) y estima cuántas personas
 * serían notificadas por push/correo — no envía push/correo reales todavía.
 *
 * PARA PRODUCCIÓN — reemplazar sendPush()/sendEmail() por integraciones reales:
 *   - Push: Firebase Cloud Messaging (guardar push token por usuario en una
 *     tabla PushToken, y llamar FirebaseMessaging.getInstance().sendMulticast(...)).
 *   - Correo: un proveedor tipo Resend, SendGrid o Amazon SES.
 */
@Service
public class NotificationService {

    private static final Map<Integer, Integer> ESTIMATE_BY_RADIUS_KM = Map.of(
        1, 18, 2, 47, 5, 134, 10, 260
    );

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification notify(UUID userId, NotificationType type, String title, String body) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setBody(body);
        return notificationRepository.save(n);
    }

    /** Estimación de alcance para una alerta SOS, según su radio de búsqueda. */
    public int estimateReach(double radiusKm) {
        return ESTIMATE_BY_RADIUS_KM.getOrDefault((int) Math.round(radiusKm), 50);
    }
}
