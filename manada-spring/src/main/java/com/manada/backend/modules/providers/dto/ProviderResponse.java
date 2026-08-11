package com.manada.backend.modules.providers.dto;

import com.manada.backend.modules.providers.ProviderProfile;
import com.manada.backend.modules.providers.ProviderType;
import com.manada.backend.modules.providers.ProviderVerificationStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProviderResponse(
    UUID id, UUID userId, ProviderType providerType, String businessName, String city,
    BigDecimal commissionRate, ProviderVerificationStatus verificationStatus
) {
    public static ProviderResponse from(ProviderProfile p) {
        return new ProviderResponse(p.getId(), p.getUserId(), p.getProviderType(), p.getBusinessName(),
            p.getCity(), p.getCommissionRate(), p.getVerificationStatus());
    }
}
