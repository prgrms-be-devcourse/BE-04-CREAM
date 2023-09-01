package com.programmers.dev.kream.purchasebidding.ui.dto;

import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;

import java.time.LocalDateTime;

public record PurchaseBiddingResponse(
        Long id,

        Long purchaseBidderId,

        Long sizedProductId,

        Long price,

        Status status,

        LocalDateTime startDate,

        LocalDateTime dueDate
) {

    public static PurchaseBiddingResponse fromEntity(PurchaseBidding purchaseBidding) {
        return new PurchaseBiddingResponse(
                purchaseBidding.getId(),
                purchaseBidding.getPurchaseBidderId(),
                purchaseBidding.getSizedProductId(),
                purchaseBidding.getPrice(),
                purchaseBidding.getStatus(),
                purchaseBidding.getStartDate(),
                purchaseBidding.getDueDate()
        );
    }
}
