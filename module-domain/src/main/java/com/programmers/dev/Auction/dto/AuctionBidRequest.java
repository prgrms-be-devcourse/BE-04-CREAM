package com.programmers.dev.Auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuctionBidRequest(
    @NotNull
    Long auctionId,

    @NotNull
    @Positive
    Long price
) {
}
