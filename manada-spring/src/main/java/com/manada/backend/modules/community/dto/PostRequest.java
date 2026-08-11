package com.manada.backend.modules.community.dto;

import com.manada.backend.modules.community.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostRequest(@NotNull PostType type, @NotBlank String caption, String photoUrl) {}
