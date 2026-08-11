package com.manada.backend.modules.adoption;

import com.manada.backend.modules.pets.PetSize;
import com.manada.backend.modules.pets.Sex;
import com.manada.backend.modules.pets.Species;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "adoption_listing")
@Getter
@Setter
@NoArgsConstructor
public class AdoptionListing {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "foundation_id", nullable = false)
    private UUID foundationId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Species species;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private PetSize size;

    @Column(name = "age_label")
    private String ageLabel;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdoptionStatus status = AdoptionStatus.DISPONIBLE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
