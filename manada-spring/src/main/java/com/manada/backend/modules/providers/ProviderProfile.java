package com.manada.backend.modules.providers;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "provider_profile")
@Getter
@Setter
@NoArgsConstructor
public class ProviderProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "tax_id")
    private String taxId;

    @Column(nullable = false)
    private String city;

    @Column(name = "documents_url")
    private String documentsUrl;

    // % que se queda Manada por cada venta/reserva de este proveedor (0.15 = 15%)
    @Column(name = "commission_rate", nullable = false)
    private BigDecimal commissionRate = new BigDecimal("0.15");

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private ProviderVerificationStatus verificationStatus = ProviderVerificationStatus.SIN_VERIFICAR;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
