package com.programmers.dev.kream.purchasebidding.ui;


import com.programmers.dev.kream.purchasebidding.application.PurchaseBiddingService;
import com.programmers.dev.kream.purchasebidding.domain.ProductSelectViewDao;
import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseSelectView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/purchase/biddings")
public class PurchaseBiddingController {

    private final PurchaseBiddingService purchaseBiddingService;

    private final ProductSelectViewDao productSelectViewDao;

    public PurchaseBiddingController(PurchaseBiddingService purchaseBiddingService, ProductSelectViewDao productSelectViewDao) {
        this.purchaseBiddingService = purchaseBiddingService;
        this.productSelectViewDao = productSelectViewDao;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<PurchaseSelectView> view(@PathVariable Long productId) {
        PurchaseSelectView purchaseView = productSelectViewDao.getPurchaseView(productId);

        return ResponseEntity.ok(purchaseView);
    }
}
