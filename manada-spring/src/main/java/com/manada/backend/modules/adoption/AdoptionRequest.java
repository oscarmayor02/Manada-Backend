package com.manada.backend.modules.adoption;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "adoption_request")
@Getter
@Setter
@NoArgsConstructor
public class AdoptionRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "listing_id", nullable = false)
    private UUID listingId;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(columnDefinition = "text")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdoptionRequestStatus status = AdoptionRequestStatus.PENDIENTE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
