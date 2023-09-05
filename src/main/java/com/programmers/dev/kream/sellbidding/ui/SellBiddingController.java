package com.programmers.dev.kream.sellbidding.ui;

import com.programmers.dev.kream.sellbidding.application.SellBiddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sell/biddings")
public class SellBiddingController {

    private final SellBiddingService sellBiddingService;

    public SellBiddingController(SellBiddingService sellBiddingService) {
        this.sellBiddingService = sellBiddingService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductInformation> getProductInformation(
            @PathVariable Long productId
    ) {
        ProductInformation productInformation = sellBiddingService.getProductInformation(productId);

        return ResponseEntity.ok(productInformation);
    }

    @PostMapping
    public ResponseEntity<SellBiddingResponse> saveSellBidding(
            @RequestParam Long userId,
            @RequestParam Long sizedProductId,
            @RequestBody SellBiddingRequest sellBiddingRequest
    ) {

        return ResponseEntity.ok(
                sellBiddingService.saveSellBidding(userId, sizedProductId, sellBiddingRequest)
        );
    }

    @PostMapping("/transact")
    public ResponseEntity<SellBiddingResponse> transactPurchaseBidding(
            @RequestParam Long userId,
            @RequestParam Long purchaseBiddingId
    ) {
        SellBiddingResponse sellBiddingResponse = sellBiddingService.transactPurchaseBidding(userId, purchaseBiddingId);

        return ResponseEntity.ok(sellBiddingResponse);
    }
}
