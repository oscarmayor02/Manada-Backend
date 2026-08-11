package com.manada.backend.modules.foundations;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "foundation_profile")
@Getter
@Setter
@NoArgsConstructor
public class FoundationProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "org_name", nullable = false)
    private String orgName;

    @Column(name = "tax_id")
    private String taxId;

    @Column(nullable = false)
    private String city;

    @Column(name = "documents_url")
    private String documentsUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.SIN_VERIFICAR;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
