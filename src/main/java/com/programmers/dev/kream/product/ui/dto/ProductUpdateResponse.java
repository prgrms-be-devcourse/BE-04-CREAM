package com.programmers.dev.kream.product.ui.dto;

import com.programmers.dev.kream.product.domain.ProductInfo;

public record ProductUpdateResponse(
    String brandName,
    String name,
    String color,
    String modelNumber
) {
}
