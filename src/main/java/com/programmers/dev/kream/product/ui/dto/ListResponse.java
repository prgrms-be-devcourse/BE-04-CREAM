package com.programmers.dev.kream.product.ui.dto;

import java.util.List;

public record ListResponse<T>(
    int size,
    List<T> data
) {
}
