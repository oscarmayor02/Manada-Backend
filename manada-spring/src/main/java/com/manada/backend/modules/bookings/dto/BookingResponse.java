package com.manada.backend.modules.bookings.dto;

import com.manada.backend.modules.bookings.Booking;
import com.manada.backend.modules.bookings.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
    UUID id, UUID serviceId, UUID buyerId, Instant scheduledAt, BookingStatus status, BigDecimal price
) {
    public static BookingResponse from(Booking b) {
        return new BookingResponse(b.getId(), b.getServiceId(), b.getBuyerId(), b.getScheduledAt(), b.getStatus(), b.getPrice());
    }
}
