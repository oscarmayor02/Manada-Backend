package com.manada.backend.modules.messaging.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(@NotBlank String text) {}
