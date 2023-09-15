package com.programmers.dev.Auction.ui;

import com.programmers.dev.Auction.application.AuctionService;
import com.programmers.dev.Auction.dto.AuctionSaveRequest;
import com.programmers.dev.Auction.dto.AuctionSaveResponse;
import com.programmers.dev.Auction.dto.AuctionStatusChangeRequest;
import com.programmers.dev.Auction.dto.AuctionStatusChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<AuctionSaveResponse> saveAuction(
        @RequestBody @Validated AuctionSaveRequest auctionSaveRequest) {
        return ResponseEntity.ok(auctionService.save(auctionSaveRequest));
    }

    @PatchMapping("/status")
    public ResponseEntity<AuctionStatusChangeResponse> changeAuctionStatus(
        @RequestBody @Validated AuctionStatusChangeRequest auctionStatusChangeRequest
    ) {
        return ResponseEntity.ok(auctionService.changeAuctionStatus(auctionStatusChangeRequest));
    }
}
