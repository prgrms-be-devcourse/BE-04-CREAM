package com.programmers.dev.bidding.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterBiddingrequest(
        @NotNull
        Long productId,
        @NotNull @Positive
        Integer price,
        @NotNull @Positive
        Long dueDate
) {
    public static RegisterBiddingrequest of(Long productId, Integer price, Long dueDate) {
        return new RegisterBiddingrequest(productId, price, dueDate);
    }
}
