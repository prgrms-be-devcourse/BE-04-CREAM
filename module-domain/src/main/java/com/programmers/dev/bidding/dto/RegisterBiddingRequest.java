package com.programmers.dev.bidding.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterBiddingRequest(
        @NotNull
        Long productId,
        @NotNull @Positive
        Integer price,
        @NotNull @Positive
        Long dueDate
) {
    public static RegisterBiddingRequest of(Long productId, Integer price, Long dueDate) {
        return new RegisterBiddingRequest(productId, price, dueDate);
    }
}
