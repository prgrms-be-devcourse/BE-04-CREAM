package com.programmers.dev.product.dto;

import java.util.List;

public record BrandsGetResponse(
    int size,
    List<BrandResponse> brandList
) {
}
