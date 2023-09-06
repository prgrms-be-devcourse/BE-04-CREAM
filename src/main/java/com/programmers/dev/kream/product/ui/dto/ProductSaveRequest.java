package com.programmers.dev.kream.product.ui.dto;

import com.programmers.dev.kream.product.domain.ProductInfo;

public record ProductSaveRequest(
    Long brandId,
    String name,
    ProductInfo productInfo
) {
}
