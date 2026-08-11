package com.manada.backend.modules.adoption.dto;

import com.manada.backend.modules.pets.PetSize;
import com.manada.backend.modules.pets.Sex;
import com.manada.backend.modules.pets.Species;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdoptionListingRequest(
    @NotBlank String name,
    @NotNull Species species,
    @NotNull Sex sex,
    PetSize size,
    String ageLabel,
    String description,
    String photoUrl
) {}
