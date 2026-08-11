package com.manada.backend.modules.users.dto;

import com.manada.backend.modules.users.AccountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password,
    @NotBlank String fullName,
    String phone,
    @NotNull AccountType accountType,

    @Valid FoundationInfo foundation,   // requerido si accountType = FUNDACION
    @Valid ProviderInfo provider        // requerido si accountType = PROVEEDOR
) {
    public record FoundationInfo(
        @NotBlank String orgName,
        String taxId,
        @NotBlank String city
    ) {}

    public record ProviderInfo(
        @NotBlank String providerType, // TIENDA | VETERINARIA | PASEADOR | PELUQUERIA | GUARDERIA | ADIESTRADOR
        @NotBlank String businessName,
        String taxId,
        @NotBlank String city
    ) {}
}
