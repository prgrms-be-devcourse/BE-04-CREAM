package com.programmers.dev.kream.product.ui.dto;

import java.util.List;

public record ProductsGetResponse(
    int size,
    List<ProductResponse> productList
) {
}
