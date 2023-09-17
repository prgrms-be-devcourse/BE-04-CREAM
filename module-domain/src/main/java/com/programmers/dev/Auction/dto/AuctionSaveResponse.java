package com.programmers.dev.Auction.dto;

public record AuctionSaveResponse(
    Long auctionId
) {
    public static AuctionSaveResponse of(Long auctionId) {
        return new AuctionSaveResponse(auctionId);
    }
}
