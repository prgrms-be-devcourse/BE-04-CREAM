package com.programmers.dev.ui.dto;

import com.programmers.dev.product.domain.ProductInfo;

public record ProductSaveRequest(
    Long brandId,
    String name,
    ProductInfo productInfo,
    int size
) {
}
