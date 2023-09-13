package com.programmers.dev.Auction.dto;

public record AuctionBidRequest(
    Long userId,
    Long auctionId,
    Long price
) {
}
