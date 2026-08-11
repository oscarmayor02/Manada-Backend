package com.manada.backend.modules.sos.dto;

import com.manada.backend.modules.sos.AlertStatus;
import com.manada.backend.modules.sos.LostPetAlert;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
    UUID id, UUID petId, UUID reporterId, AlertStatus status, String lastSeenAddress,
    String locality, BigDecimal radiusKm, String contactPhone, String notes,
    boolean notifyPush, boolean notifyEmail, Instant createdAt, Instant resolvedAt
) {
    public static AlertResponse from(LostPetAlert a) {
        return new AlertResponse(a.getId(), a.getPetId(), a.getReporterId(), a.getStatus(), a.getLastSeenAddress(),
            a.getLocality(), a.getRadiusKm(), a.getContactPhone(), a.getNotes(), a.isNotifyPush(), a.isNotifyEmail(),
            a.getCreatedAt(), a.getResolvedAt());
    }
}
