package com.programmers.dev.bidding.ui;

import com.programmers.dev.bidding.application.BiddingService;
import com.programmers.dev.bidding.dto.BiddingResponse;
import com.programmers.dev.bidding.dto.RegisterBiddingRequest;
import com.programmers.dev.bidding.dto.TransactBiddingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
            @RequestParam Long userId,
            @RequestBody @Valid RegisterBiddingRequest request
    ) {
        BiddingResponse biddingResponse = biddingService.registerPurchaseBidding(userId, request);
        return ResponseEntity.created(URI.create(url + "/purchase")).body(biddingResponse);
    }

    @PostMapping("/purchase-now")
    public ResponseEntity<BiddingResponse> transactSellBidding(
            @RequestParam Long userId,
            @RequestBody @Valid TransactBiddingRequest request
    ) {
        BiddingResponse biddingResponse = biddingService.transactSellBidding(userId, request);
        return ResponseEntity.created(URI.create(url + "/purchase-now")).body(biddingResponse);
    }

    @PostMapping("/sell")
    public ResponseEntity<BiddingResponse> registerSellBidding(
            @RequestParam Long userId,
            @RequestBody @Valid RegisterBiddingRequest request
    ) {
        BiddingResponse biddingResponse = biddingService.registerSellBidding(userId, request);
        return ResponseEntity.created(URI.create(url + "/sell")).body(biddingResponse);
    }

    @PostMapping("/sell-now")
    public ResponseEntity<BiddingResponse> transactPurchaseBidding(
            @RequestParam Long userId,
            @RequestBody @Valid TransactBiddingRequest request
    ) {
        BiddingResponse biddingResponse = biddingService.transactPurchaseBidding(userId, request);
        return ResponseEntity.created(URI.create(url + "/sell-now")).body(biddingResponse);
    }


}