package com.programmers.dev.kream.purchasebidding.ui.dto;


import java.util.List;

public record PurchaseSelectView(
        String productName,
        List<BiddingSelectLine> biddingSelectLines
) {
}
