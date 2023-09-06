package com.programmers.dev.kream.purchasebidding.domain;

import com.programmers.dev.kream.purchasebidding.ui.dto.BiddingSelectLine;

import java.util.List;

public interface PurchaseSelectViewDao {

    List<BiddingSelectLine> getPurchaseView(Long productId);
}
