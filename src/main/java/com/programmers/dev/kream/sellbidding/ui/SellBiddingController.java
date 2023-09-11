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

    @GetMapping
    public ResponseEntity<ProductInformation> getProductInformation(
            @RequestParam String productName,
            @RequestParam String brandName
    ) {
        ProductInformation productInformation = sellBiddingService.getProductInformation(productName, brandName);

        return ResponseEntity.ok(productInformation);
    }

    @PostMapping
    public ResponseEntity<SellBiddingResponse> saveSellBidding(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestBody SellBiddingRequest sellBiddingRequest
    ) {

        return ResponseEntity.ok(
                sellBiddingService.saveSellBidding(userId, productId, sellBiddingRequest)
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
