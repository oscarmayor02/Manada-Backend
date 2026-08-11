package com.manada.backend.modules.adoption.dto;

import com.manada.backend.modules.adoption.AdoptionListing;
import com.manada.backend.modules.adoption.AdoptionStatus;
import com.manada.backend.modules.pets.PetSize;
import com.manada.backend.modules.pets.Sex;
import com.manada.backend.modules.pets.Species;

import java.util.UUID;

public record AdoptionListingResponse(
    UUID id, UUID foundationId, String name, Species species, Sex sex, PetSize size,
    String ageLabel, String description, String photoUrl, AdoptionStatus status
) {
    public static AdoptionListingResponse from(AdoptionListing a) {
        return new AdoptionListingResponse(a.getId(), a.getFoundationId(), a.getName(), a.getSpecies(), a.getSex(),
            a.getSize(), a.getAgeLabel(), a.getDescription(), a.getPhotoUrl(), a.getStatus());
    }
}
