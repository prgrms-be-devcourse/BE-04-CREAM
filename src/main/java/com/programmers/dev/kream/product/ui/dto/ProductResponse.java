package com.programmers.dev.kream.product.ui.dto;

import com.programmers.dev.kream.product.domain.Product;
import com.programmers.dev.kream.product.domain.ProductInfo;

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
