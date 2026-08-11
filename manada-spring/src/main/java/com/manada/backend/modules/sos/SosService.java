package com.manada.backend.modules.sos;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.modules.notifications.NotificationService;
import com.manada.backend.modules.notifications.NotificationType;
import com.manada.backend.modules.pets.Pet;
import com.manada.backend.modules.pets.PetRepository;
import com.manada.backend.modules.sos.dto.AlertResponse;
import com.manada.backend.modules.sos.dto.CreateAlertRequest;
import com.manada.backend.modules.sos.dto.SightingRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SosService {

    private final LostPetAlertRepository alertRepository;
    private final SightingRepository sightingRepository;
    private final PetRepository petRepository;
    private final NotificationService notificationService;

    public SosService(
            LostPetAlertRepository alertRepository,
            SightingRepository sightingRepository,
            PetRepository petRepository,
            NotificationService notificationService
    ) {
        this.alertRepository = alertRepository;
        this.sightingRepository = sightingRepository;
        this.petRepository = petRepository;
        this.notificationService = notificationService;
    }

    public List<AlertResponse> listActive() {
        return alertRepository
            .findByStatusInOrderByCreatedAtDesc(List.of(AlertStatus.PERDIDO, AlertStatus.AVISTAMIENTO_REPORTADO))
            .stream().map(AlertResponse::from).toList();
    }

    @Transactional
    public record CreateResult(AlertResponse alert, int estimatedReach) {}

    @Transactional
    public CreateResult create(UUID reporterId, CreateAlertRequest req) {
        Pet pet = petRepository.findById(req.petId())
            .orElseThrow(() -> ApiException.notFound("Mascota no encontrada."));
        if (!pet.getOwnerId().equals(reporterId)) {
            throw ApiException.forbidden("Solo el dueño puede reportar esta mascota como perdida.");
        }

        LostPetAlert alert = new LostPetAlert();
        alert.setPetId(pet.getId());
        alert.setReporterId(reporterId);
        alert.setLastSeenAddress(req.lastSeenAddress());
        alert.setLocality(req.locality());
        if (req.radiusKm() != null) alert.setRadiusKm(BigDecimal.valueOf(req.radiusKm()));
        alert.setContactPhone(req.contactPhone());
        alert.setNotes(req.notes());
        if (req.notifyPush() != null) alert.setNotifyPush(req.notifyPush());
        if (req.notifyEmail() != null) alert.setNotifyEmail(req.notifyEmail());

        alert = alertRepository.save(alert);

        int estimatedReach = notificationService.estimateReach(alert.getRadiusKm().doubleValue());
        // Punto de integración real de push/correo — ver NotificationService.

        return new CreateResult(AlertResponse.from(alert), estimatedReach);
    }

    @Transactional
    public Sighting reportSighting(UUID alertId, UUID reporterId, SightingRequest req) {
        LostPetAlert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> ApiException.notFound("Alerta no encontrada."));

        Sighting sighting = new Sighting();
        sighting.setAlertId(alertId);
        sighting.setReporterId(reporterId);
        sighting.setNote(req.note());
        sighting.setPhotoUrl(req.photoUrl());
        sighting.setLatitude(req.latitude());
        sighting.setLongitude(req.longitude());
        sighting = sightingRepository.save(sighting);

        alert.setStatus(AlertStatus.AVISTAMIENTO_REPORTADO);
        alertRepository.save(alert);

        notificationService.notify(
            alert.getReporterId(),
            NotificationType.SOS,
            "Alguien vio a tu mascota",
            req.note() != null ? req.note() : "Revisa los detalles del avistamiento en la app."
        );

        return sighting;
    }

    @Transactional
    public AlertResponse resolve(UUID alertId, UUID requesterId) {
        LostPetAlert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> ApiException.notFound("Alerta no encontrada."));
        if (!alert.getReporterId().equals(requesterId)) {
            throw ApiException.forbidden("Solo quien reportó la alerta puede cerrarla.");
        }
        alert.setStatus(AlertStatus.RESUELTO);
        alert.setResolvedAt(Instant.now());
        return AlertResponse.from(alertRepository.save(alert));
    }
}
