package com.manada.backend.modules.sos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sighting")
@Getter
@Setter
@NoArgsConstructor
public class Sighting {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "alert_id", nullable = false)
    private UUID alertId;

    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "photo_url")
    private String photoUrl;

    private Double latitude;
    private Double longitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
