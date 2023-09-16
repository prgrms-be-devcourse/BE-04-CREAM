package com.programmers.dev.bidding.dto;

public record DepositResponse(
        String message
) {
    public static DepositResponse of(String message) {
        return new DepositResponse(message);
    }
}
