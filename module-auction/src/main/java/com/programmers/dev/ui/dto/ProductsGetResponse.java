package com.programmers.dev.ui.dto;

import java.util.List;

public record ProductsGetResponse(
    int size,
    List<ProductResponse> productList
) {
}
