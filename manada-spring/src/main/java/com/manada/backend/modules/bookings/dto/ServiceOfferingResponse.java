package com.manada.backend.modules.bookings.dto;

import com.manada.backend.modules.bookings.ServiceOffering;
import com.manada.backend.modules.bookings.ServiceType;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceOfferingResponse(
    UUID id, UUID providerId, ServiceType serviceType, String name, BigDecimal price, Integer durationMin
) {
    public static ServiceOfferingResponse from(ServiceOffering s) {
        return new ServiceOfferingResponse(s.getId(), s.getProviderId(), s.getServiceType(), s.getName(), s.getPrice(), s.getDurationMin());
    }
}
