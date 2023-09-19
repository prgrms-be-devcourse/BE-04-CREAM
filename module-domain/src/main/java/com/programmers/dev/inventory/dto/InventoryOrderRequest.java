package com.programmers.dev.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventoryOrderRequest(
        @Positive(message = "가격은 0원보다 높아야 합니다.")
        Long price,

        @NotNull(message = "상품 아이디는 NULL 값을 가질 수 없습니다.")
        Long productId
) {
}
