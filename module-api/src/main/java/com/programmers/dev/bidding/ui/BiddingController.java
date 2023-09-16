package com.programmers.dev.bidding.ui;

import com.programmers.dev.bidding.application.BiddingService;
import com.programmers.dev.bidding.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/bidding")
@RequiredArgsConstructor
public class BiddingController {

    @Value("${url}")
    String url;

    private final BiddingService biddingService;

    @PostMapping("/purchase")
    public ResponseEntity<BiddingResponse> registerPurchaseBidding(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false, defaultValue = "delivery") String storage,
            @RequestBody @Valid RegisterBiddingRequest request
    ) {
        BiddingResponse biddingResponse = biddingService.registerPurchaseBidding(userId, storage, request);
        return ResponseEntity.created(URI.create(url + "/purchase")).body(biddingResponse);
    }

    @PostMapping("/purchase-now")
    public ResponseEntity<BiddingResponse> transactSellBidding(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false, defaultValue = "delivery") String storage,
            @RequestBody @Valid TransactBiddingRequest request
    ) {
        BiddingResponse biddingResponse = biddingService.transactSellBidding(userId, storage, request);
        return ResponseEntity.created(URI.create(url + "/purchase-now")).body(biddingResponse);
    }

    @PostMapping("/sell")
    public ResponseEntity<BiddingResponse> registerSellBidding(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid RegisterBiddingRequest request
    ) {
        BiddingResponse biddingResponse = biddingService.registerSellBidding(userId, request);
        return ResponseEntity.created(URI.create(url + "/sell")).body(biddingResponse);
    }

    @PostMapping("/sell-now")
    public ResponseEntity<BiddingResponse> transactPurchaseBidding(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid TransactBiddingRequest request
    ) {
        BiddingResponse biddingResponse = biddingService.transactPurchaseBidding(userId, request);
        return ResponseEntity.created(URI.create(url + "/sell-now")).body(biddingResponse);
    }

    @PostMapping("/inspect/{biddingId}")
    public ResponseEntity<BiddingMessageResponse> inspectBiddingProduct(
            @PathVariable Long biddingId,
            @RequestParam String result
    ) {
        biddingService.inspect(biddingId, result);
        return ResponseEntity.ok().
                body(BiddingMessageResponse.of(makeResponse(result)));
    }

    private String makeResponse(String result) {
        String message;
        if ("ok".equalsIgnoreCase(result)) {
            message = "biddingProduct is authenticated";
        } else {
            message = "biddingProduct is rejected";
        }
        return message;
    }

    @PostMapping("/deposit/{biddingId}")
    public ResponseEntity<BiddingMessageResponse> deposit(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long biddingId
    ) {
        biddingService.deposit(userId, biddingId);

        return ResponseEntity.ok(BiddingMessageResponse.of("successfully deposited"));
    }

    @PostMapping("/finish/{biddingId}")
    public ResponseEntity<BiddingMessageResponse> finish(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long biddingId
    ) {
        biddingService.finish(userId, biddingId);
        return ResponseEntity.ok(BiddingMessageResponse.of("successfully finished"));
    }

}
