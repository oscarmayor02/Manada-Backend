package com.manada.backend.modules.community.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(@NotBlank String text) {}
