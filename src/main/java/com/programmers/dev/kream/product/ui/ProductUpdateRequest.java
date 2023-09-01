package com.programmers.dev.kream.product.ui;

import java.time.LocalDateTime;

public record ProductUpdateRequest(
    Long brandId,
    String productName,
    String modelNumber,
    LocalDateTime releaseDate,
    String color,
    Long releasePrice
) {
}
