package com.manada.backend.modules.providers.dto;

import jakarta.validation.constraints.NotNull;

public record VerifyProviderRequest(@NotNull String status) {}
