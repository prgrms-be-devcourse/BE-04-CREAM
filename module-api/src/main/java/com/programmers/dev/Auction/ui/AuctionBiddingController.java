package com.programmers.dev.Auction.ui;

import com.programmers.dev.Auction.application.AuctionBiddingService;
import com.programmers.dev.Auction.dto.AuctionBidRequest;
import com.programmers.dev.Auction.dto.AuctionBidResponse;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions/bidding")
public class AuctionBiddingController {

    private final AuctionBiddingService auctionBiddingService;

    @PostMapping
    public ResponseEntity<AuctionBidResponse> bidAuction(
        @AuthenticationPrincipal Long userId,
        @RequestBody @Validated AuctionBidRequest auctionBidRequest,
        BindingResult bindingResult
    ) {
        validateRequestBody(bindingResult);

        return ResponseEntity.ok(auctionBiddingService.bidAuction(userId, auctionBidRequest));
    }

    private static void validateRequestBody(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CreamException(ErrorCode.INVALID_REQUEST_VALUE);
        }
    }
}
