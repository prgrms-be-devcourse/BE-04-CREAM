package com.programmers.dev.bidding.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterPurchaseBiddingRequest(
        @NotNull
        Long productId,
        @NotNull @Positive
        Integer price,
        @NotNull @Positive
        Long dueDate
) {
    public static RegisterPurchaseBiddingRequest of( Long productId, Integer price, Long dueDate) {
        return new RegisterPurchaseBiddingRequest(productId, price, dueDate);
    }
}
