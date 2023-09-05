package com.programmers.dev.kream.purchasebidding.ui;


import com.programmers.dev.kream.product.application.ProductService;
import com.programmers.dev.kream.product.domain.Product;
import com.programmers.dev.kream.product.ui.dto.ProductResponse;
import com.programmers.dev.kream.purchasebidding.application.PurchaseBiddingService;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseSelectViewDao;
import com.programmers.dev.kream.purchasebidding.ui.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/purchase/biddings")
public class PurchaseBiddingController {

    private final PurchaseBiddingService purchaseBiddingService;

    private final PurchaseSelectViewDao productSelectViewDao;

    private final ProductService productService;

    public PurchaseBiddingController(PurchaseBiddingService purchaseBiddingService, PurchaseSelectViewDao productSelectViewDao, ProductService productService) {
        this.purchaseBiddingService = purchaseBiddingService;
        this.productSelectViewDao = productSelectViewDao;
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<PurchaseSelectView> getProductSelectView(@PathVariable Long productId) {
        ProductResponse product = productService.findById(productId);
        List<BiddingSelectLine> biddingSelectLines = productSelectViewDao.getPurchaseView(productId);

        return ResponseEntity.ok(new PurchaseSelectView(product.name(), biddingSelectLines));
    }

    @PostMapping("/purchase-now")
    public ResponseEntity<PurchaseBiddingResponse> purchaseNow(@RequestBody PurchaseBiddingNowRequest request) {
        Long purchaseBiddingId = purchaseBiddingService.purchaseNow(getPurchaserId(), request);

        return ResponseEntity.ok(new PurchaseBiddingResponse(purchaseBiddingId));
    }

    @PostMapping("/bid")
    public ResponseEntity<PurchaseBiddingResponse> bid(@RequestBody PurchaseBiddingBidRequest request) {
        Long purchaseBiddingId = purchaseBiddingService.bid(getPurchaserId(), request);

        return ResponseEntity.ok(new PurchaseBiddingResponse(purchaseBiddingId));
    }

    private long getPurchaserId() {
        return 1L;
    }
}
