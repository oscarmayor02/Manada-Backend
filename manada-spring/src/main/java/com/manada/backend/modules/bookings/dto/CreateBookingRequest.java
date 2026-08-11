package com.manada.backend.modules.bookings.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingRequest(@NotNull UUID serviceId, @NotNull Instant scheduledAt) {}
