package com.programmers.dev.kream.purchasebidding.domain;

import com.programmers.dev.kream.purchasebidding.ui.dto.SelectLine;

import java.util.List;

public interface ProductSelectViewDao {

    List<SelectLine> getPurchaseView(Long productId);
}
