package com.programmers.dev.bidding.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactSellBiddingRequest(
        @NotNull @Positive
        Long biddingId

) {
}
