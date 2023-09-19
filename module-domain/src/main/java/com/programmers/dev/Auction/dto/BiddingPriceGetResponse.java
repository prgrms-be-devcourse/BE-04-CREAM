package com.programmers.dev.Auction.dto;

public record BiddingPriceGetResponse(
    Long auctionId,
    Long price

) {
    public static BiddingPriceGetResponse of(Long auctionId, Long price) {
        return new BiddingPriceGetResponse(auctionId, price);
    }
}
