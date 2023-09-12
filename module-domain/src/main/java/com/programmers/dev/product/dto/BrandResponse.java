package com.programmers.dev.product.dto;


import com.programmers.dev.product.domain.Brand;

public record BrandResponse(
    Long id,
    String name
) {
    public static BrandResponse fromEntity(Brand brand) {
        return new BrandResponse(brand.getId(), brand.getName());
    }
}
