package com.programmers.dev.bidding.dto;

public record InspectResponse(
        String message
) {

    public static InspectResponse of(String message) {
        return new InspectResponse(message);
    }
}
