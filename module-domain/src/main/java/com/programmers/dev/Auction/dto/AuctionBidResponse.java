package com.programmers.dev.Auction.dto;

import com.programmers.dev.Auction.domain.AuctionBidding;

public record AuctionBidResponse(
    Long auctionBiddingId,
    Long userId,
    Long price
) {
    public static AuctionBidResponse fromEntity(AuctionBidding auctionBidding) {
        return new AuctionBidResponse(
            auctionBidding.getId(),
            auctionBidding.getUser().getId(),
            auctionBidding.getPrice());
    }
}
