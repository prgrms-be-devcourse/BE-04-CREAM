package com.programmers.dev.kream.product.ui.dto;

import java.util.List;

public record BrandsGetResponse(
    int size,
    List<BrandResponse> brandList
) {
}
