package com.manada.backend.modules.pets.dto;

import com.manada.backend.modules.pets.Pet;
import com.manada.backend.modules.pets.PetSize;
import com.manada.backend.modules.pets.Sex;
import com.manada.backend.modules.pets.Species;

import java.time.Instant;
import java.util.UUID;

public record PetResponse(
    UUID id, UUID ownerId, String name, Species species, Sex sex, PetSize size,
    String breed, Integer birthYear, String photoUrl, UUID qrCode, Instant createdAt
) {
    public static PetResponse from(Pet p) {
        return new PetResponse(p.getId(), p.getOwnerId(), p.getName(), p.getSpecies(), p.getSex(), p.getSize(),
            p.getBreed(), p.getBirthYear(), p.getPhotoUrl(), p.getQrCode(), p.getCreatedAt());
    }
}
