package com.programmers.dev.kream.product.ui.dto;

public record SizedProductResponse(
    Long id,
    ProductResponse productResponse,
    int size
) {
    public static SizedProductResponse fromEntity(SizedProduct sizedProduct) {
        return new SizedProductResponse(
            sizedProduct.getId(),
            ProductResponse.fromEntity(sizedProduct.getProduct()),
            sizedProduct.getSize());
    }
}
