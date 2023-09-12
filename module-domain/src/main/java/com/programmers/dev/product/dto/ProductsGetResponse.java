package com.programmers.dev.product.dto;

import java.util.List;

public record ProductsGetResponse(
    int size,
    List<ProductResponse> productList
) {
}
