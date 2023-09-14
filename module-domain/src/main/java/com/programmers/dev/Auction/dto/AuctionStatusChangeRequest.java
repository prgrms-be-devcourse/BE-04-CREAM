package com.programmers.dev.Auction.dto;

import com.programmers.dev.common.AuctionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AuctionStatusChangeRequest(
    @NotNull
    Long id,

    @NotNull
    AuctionStatus auctionStatus
) {
}
