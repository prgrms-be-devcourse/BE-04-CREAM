package com.programmers.dev.Auction.dto;

public record BidderDecisionResponse(
    Long userId,

    Boolean purchaseStatus,

    Long price

) {
    public static BidderDecisionResponse of(Long userId, Boolean purchaseStatus, Long price) {
        return new BidderDecisionResponse(userId, purchaseStatus, price);
    }
}
