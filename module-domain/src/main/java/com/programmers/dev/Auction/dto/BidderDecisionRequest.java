package com.programmers.dev.Auction.dto;

public record BidderDecisionRequest(
    Long auctionId,
    Boolean purchaseStatus,
    Long price

) {
}
