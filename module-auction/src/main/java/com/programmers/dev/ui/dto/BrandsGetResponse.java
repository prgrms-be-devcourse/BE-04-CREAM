package com.programmers.dev.ui.dto;

import java.util.List;

public record BrandsGetResponse(
    int size,
    List<BrandResponse> brandList
) {
}
