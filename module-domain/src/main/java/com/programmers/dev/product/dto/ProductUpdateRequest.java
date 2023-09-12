package com.programmers.dev.product.dto;

public record ProductUpdateRequest(
    Long brandId,
    String productName,
    String modelNumber,
    String color
) {
}
