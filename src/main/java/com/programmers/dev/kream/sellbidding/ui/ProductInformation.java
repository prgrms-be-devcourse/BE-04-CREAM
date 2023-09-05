package com.programmers.dev.kream.sellbidding.ui;

import java.util.List;

public record ProductInformation(
        String productName,
        List<SizeInformation> biddingSelectLines
) {
}
