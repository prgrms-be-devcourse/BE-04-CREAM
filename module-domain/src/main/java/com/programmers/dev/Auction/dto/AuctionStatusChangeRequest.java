package com.programmers.dev.Auction.dto;

import com.programmers.dev.common.AuctionStatus;

public record AuctionStatusChangeRequest(
    Long id,
    AuctionStatus auctionStatus
) {
}
