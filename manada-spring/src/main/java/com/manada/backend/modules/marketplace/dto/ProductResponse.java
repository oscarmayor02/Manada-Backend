package com.manada.backend.modules.marketplace.dto;

import com.manada.backend.modules.marketplace.Product;
import com.manada.backend.modules.marketplace.ProductCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
    UUID id, UUID providerId, String name, ProductCategory category,
    BigDecimal price, Integer stock, String photoUrl, boolean active
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getProviderId(), p.getName(), p.getCategory(),
            p.getPrice(), p.getStock(), p.getPhotoUrl(), p.isActive());
    }
}
