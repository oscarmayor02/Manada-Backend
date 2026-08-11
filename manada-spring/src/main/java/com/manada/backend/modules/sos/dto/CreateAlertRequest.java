package com.manada.backend.modules.sos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateAlertRequest(
    @NotNull UUID petId,
    @NotBlank String lastSeenAddress,
    String locality,
    @Positive Double radiusKm,
    @NotBlank String contactPhone,
    String notes,
    Boolean notifyPush,
    Boolean notifyEmail
) {}
