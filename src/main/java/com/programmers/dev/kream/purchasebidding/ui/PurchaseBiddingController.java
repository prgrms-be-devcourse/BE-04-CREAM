package com.programmers.dev.kream.purchasebidding.ui;


import com.programmers.dev.kream.product.application.ProductService;
import com.programmers.dev.kream.product.domain.Product;
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

    /**
     * [상품 구입 선택 화면]
     * @param productId : 구입하고 싶은 제품번호
     * @return : 제품이름, 사이즈 별 최저가
     */
    @GetMapping("/{productId}")
    public ResponseEntity<PurchaseSelectView> getProductSelectView(@PathVariable Long productId) {
        Product product = productService.findById(productId);
        List<BiddingSelectLine> biddingSelectLines = productSelectViewDao.getPurchaseView(productId);

        return ResponseEntity.ok(new PurchaseSelectView(product.getName(), biddingSelectLines));
    }

    /**
     * [즉시 구매]
     * @param request : 구매가격, 상품번호
     * @return : 구매입찰ID
     */
    @PostMapping("/purchase-now")
    public ResponseEntity<PurchaseBiddingResponse> purchaseNow(@RequestBody PurchaseBiddingNowRequest request) {
        Long purchaseBiddingId = purchaseBiddingService.purchaseNow(getPurchaserId(), request);

        return ResponseEntity.ok(new PurchaseBiddingResponse(purchaseBiddingId));
    }

    /**
     * [입찰 구매]
     * @param request : 입찰희망가격, 상품번호, 입찰기간
     * @return : 구매입찰ID
     */
    @PostMapping("/bid")
    public ResponseEntity<PurchaseBiddingResponse> bid(@RequestBody PurchaseBiddingBidRequest request) {
        Long purchaseBiddingId = purchaseBiddingService.bid(getPurchaserId(), request);

        return ResponseEntity.ok(new PurchaseBiddingResponse(purchaseBiddingId));
    }

    private long getPurchaserId() {
        return 1L;
    }
}
