package com.programmers.dev.Auction.dto;

import com.programmers.dev.common.AuctionStatus;

public record AuctionStatusChangeResponse(
    Long auctionId,

    AuctionStatus auctionStatus
) {
    public static AuctionStatusChangeResponse of(Long auctionId, AuctionStatus auctionStatus) {
        return new AuctionStatusChangeResponse(auctionId, auctionStatus);
    }
}
