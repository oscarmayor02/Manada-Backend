package com.manada.backend.modules.pets;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pet")
@Getter
@Setter
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

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

    private String breed;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "qr_code", nullable = false, unique = true)
    private UUID qrCode = UUID.randomUUID();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
