package com.programmers.dev.bidding.dto;

public record BiddingResponse(
        Long biddingId
) {

    public static BiddingResponse of(Long biddingId) {
        return new BiddingResponse(biddingId);
    }
}
