package com.manada.backend.modules.pets.dto;

import com.manada.backend.modules.pets.PetSize;
import com.manada.backend.modules.pets.Sex;
import com.manada.backend.modules.pets.Species;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PetRequest(
    @NotBlank String name,
    @NotNull Species species,
    @NotNull Sex sex,
    PetSize size,
    String breed,
    Integer birthYear,
    String photoUrl
) {}
