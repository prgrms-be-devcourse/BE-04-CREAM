package com.programmers.dev.Auction.dto;

import jakarta.validation.constraints.NotNull;

public record BiddingPriceGetRequest(
    @NotNull
    Long auctionId
) {
}
