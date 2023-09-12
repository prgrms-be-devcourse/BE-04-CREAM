package com.programmers.dev.ui.dto;

public record ProductUpdateResponse(
    String brandName,
    String name,
    String color,
    String modelNumber
) {
}
