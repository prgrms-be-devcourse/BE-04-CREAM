package com.programmers.dev.bidding.dto;

public record BiddingMessageResponse(
        String message
) {

    public static BiddingMessageResponse of(String message) {
        return new BiddingMessageResponse(message);
    }
}
