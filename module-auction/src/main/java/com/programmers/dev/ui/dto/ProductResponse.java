package com.programmers.dev.ui.dto;

import com.programmers.dev.product.domain.Product;
import com.programmers.dev.product.domain.ProductInfo;

public record ProductResponse(
    Long id,
    BrandResponse brand,
    String name,
    ProductInfo productInfo
) {
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
            product.getId(),
            BrandResponse.fromEntity(product.getBrand()),
            product.getName(),
            product.getProductInfo());
    }
}
