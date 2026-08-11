package com.manada.backend.modules.bookings.dto;

import com.manada.backend.modules.bookings.ServiceType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ServiceOfferingRequest(
    @NotNull ServiceType serviceType,
    @NotBlank String name,
    @NotNull @Positive BigDecimal price,
    @NotNull @Positive Integer durationMin
) {}
