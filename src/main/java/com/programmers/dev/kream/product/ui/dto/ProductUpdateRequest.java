package com.programmers.dev.kream.product.ui.dto;

public record ProductUpdateRequest(
    Long brandId,
    String productName,
    String modelNumber,
    String color,
    Long releasePrice
) {
}
