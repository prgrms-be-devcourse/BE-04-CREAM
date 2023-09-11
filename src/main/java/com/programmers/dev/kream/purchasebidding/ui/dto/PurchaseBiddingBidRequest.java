package com.programmers.dev.kream.purchasebidding.ui.dto;

import com.programmers.dev.kream.common.bidding.BiddingDuration;

public record PurchaseBiddingBidRequest(
        Long price,
        Long productId,
        BiddingDuration biddingDuration
) {
}
