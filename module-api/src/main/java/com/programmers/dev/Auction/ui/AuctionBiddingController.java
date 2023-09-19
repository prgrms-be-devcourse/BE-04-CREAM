package com.programmers.dev.Auction.ui;

import com.programmers.dev.Auction.application.AuctionBiddingService;
import com.programmers.dev.Auction.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions/bidding")
public class AuctionBiddingController {

    private final AuctionBiddingService auctionBiddingService;

    @PostMapping
    public ResponseEntity<AuctionBidResponse> bidAuction(
        @AuthenticationPrincipal Long userId,
        @RequestBody @Validated AuctionBidRequest auctionBidRequest
    ) {
        return ResponseEntity.ok(auctionBiddingService.bidAuction(userId, auctionBidRequest));
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelAuctionBidding(
        @AuthenticationPrincipal Long userId,
        @RequestBody @Validated AuctionBiddingCancelRequest request
    ) {
        auctionBiddingService.cancelAuctionBid(userId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current-price")
    public ResponseEntity<BiddingPriceGetResponse> getCurrentBiddingPrice(
        @AuthenticationPrincipal Long userId,
        @RequestBody @Validated BiddingPriceGetRequest request
        ) {
        return ResponseEntity.ok().body(auctionBiddingService.getCurrentBiddingPrice(request));
    }

    @PostMapping("/decision")
    public ResponseEntity<BidderDecisionResponse> getBidderDecision(
        @AuthenticationPrincipal Long userId,
        @RequestBody @Validated BidderDecisionRequest request
    ) {
        return ResponseEntity.ok().body(auctionBiddingService.decidePurchaseStatus(userId, request));
    }
}
