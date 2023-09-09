package com.programmers.dev.kream.purchasebidding.ui.dto;

import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;


public record PurchaseBiddingResponse(
        Long purchaseBiddingId
) {
    public static PurchaseBiddingResponse fromEntity(PurchaseBidding purchaseBidding) {
        return new PurchaseBiddingResponse(purchaseBidding.getPrice());
    }
}
