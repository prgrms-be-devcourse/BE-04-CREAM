package com.programmers.dev.settlement.domain;


public record SettlementConfirmedEvent(
        Long userId,
        Long money
) {
}
