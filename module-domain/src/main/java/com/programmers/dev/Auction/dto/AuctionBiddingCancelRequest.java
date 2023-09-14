package com.programmers.dev.Auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuctionBiddingCancelRequest(

    @NotNull
    Long auctionId,

    @NotNull
    @Positive
    Long price
) {
}
