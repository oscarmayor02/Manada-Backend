package com.manada.backend.modules.adoption.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateRequestStatusDto(@NotNull String status) {}
