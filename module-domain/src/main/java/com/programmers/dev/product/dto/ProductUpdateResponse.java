package com.programmers.dev.product.dto;

public record ProductUpdateResponse(
    String brandName,
    String name,
    String color,
    String modelNumber
) {
}
