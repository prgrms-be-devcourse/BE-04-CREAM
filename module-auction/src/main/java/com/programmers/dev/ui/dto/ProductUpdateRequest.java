package com.programmers.dev.ui.dto;

public record ProductUpdateRequest(
    Long brandId,
    String productName,
    String modelNumber,
    String color
) {
}
