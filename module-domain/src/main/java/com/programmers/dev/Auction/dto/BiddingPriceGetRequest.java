package com.programmers.dev.Auction.dto;

import jakarta.validation.constraints.NotNull;

public record BiddingPriceGetRequest(
    @NotNull(message = "입찰 최고가를 알고싶은 경매의 ID를 입력해주세요.")
    Long auctionId
) {
    public static BiddingPriceGetRequest of(Long auctionId) {
        return new BiddingPriceGetRequest(auctionId);
    }
}
