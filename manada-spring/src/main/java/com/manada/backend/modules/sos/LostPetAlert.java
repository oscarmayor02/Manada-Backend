package com.manada.backend.modules.sos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lost_pet_alert")
@Getter
@Setter
@NoArgsConstructor
public class LostPetAlert {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "pet_id", nullable = false)
    private UUID petId;

    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status = AlertStatus.PERDIDO;

    @Column(name = "last_seen_address", nullable = false)
    private String lastSeenAddress;

    private String locality;
    private Double latitude;
    private Double longitude;

    @Column(name = "radius_km", nullable = false)
    private BigDecimal radiusKm = new BigDecimal("2");

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "notify_push", nullable = false)
    private boolean notifyPush = true;

    @Column(name = "notify_email", nullable = false)
    private boolean notifyEmail = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
