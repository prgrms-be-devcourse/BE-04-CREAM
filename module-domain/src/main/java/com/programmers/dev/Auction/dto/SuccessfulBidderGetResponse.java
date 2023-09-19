package com.programmers.dev.Auction.dto;

public record SuccessfulBidderGetResponse(
    Long auctionId,
    Long userId,
    Long price
) {
    public static SuccessfulBidderGetResponse of(Long auctionId, Long userId, Long price) {
        return new SuccessfulBidderGetResponse(auctionId, userId, price);
    }
}
