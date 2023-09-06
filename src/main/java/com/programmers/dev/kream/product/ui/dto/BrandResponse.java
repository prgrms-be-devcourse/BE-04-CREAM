package com.programmers.dev.kream.product.ui.dto;

import com.programmers.dev.kream.product.domain.Brand;

public record BrandResponse(
    Long id,
    String name
) {
    public static BrandResponse fromEntity(Brand brand) {
        return new BrandResponse(brand.getId(), brand.getName());
    }
}
