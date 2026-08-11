package com.manada.backend.modules.marketplace.dto;

import com.manada.backend.modules.marketplace.ProductCategory;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank String name,
    @NotNull ProductCategory category,
    @NotNull @Positive BigDecimal price,
    @NotNull @PositiveOrZero Integer stock,
    String photoUrl
) {}
