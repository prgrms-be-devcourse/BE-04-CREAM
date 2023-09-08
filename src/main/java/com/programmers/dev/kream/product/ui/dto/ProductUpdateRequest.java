package com.programmers.dev.kream.product.ui.dto;

import java.time.LocalDateTime;

public record ProductUpdateRequest(
    Long brandId,
    String productName,
    String modelNumber,
    String color
) {
}
