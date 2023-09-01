package com.programmers.dev.kream.purchasebidding.domain;

import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseSelectView;

import java.util.List;

public interface ProductSelectViewDao {

    PurchaseSelectView getPurchaseView(Long productId);

}
