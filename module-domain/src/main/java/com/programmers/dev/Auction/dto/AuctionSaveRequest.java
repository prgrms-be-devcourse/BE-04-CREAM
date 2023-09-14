package com.programmers.dev.Auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AuctionSaveRequest(
    @NotNull
    Long productId,

    @NotNull
    @Positive
    Long startPrice,

    @NotNull
    LocalDateTime startTime,

    @NotNull
    LocalDateTime endTime) {
}
