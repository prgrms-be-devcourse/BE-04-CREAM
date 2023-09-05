package com.programmers.dev.kream.purchasebidding.ui.dto;

import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;

import java.time.LocalDateTime;

public record PurchaseBiddingResponse(
        Long purchaseBiddingId
) {
    public static PurchaseBiddingResponse fromEntity(PurchaseBidding purchaseBidding) {
        return new PurchaseBiddingResponse(purchaseBidding.getPrice());
    }
}
