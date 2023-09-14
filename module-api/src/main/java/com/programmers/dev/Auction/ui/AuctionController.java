package com.programmers.dev.Auction.ui;

import com.programmers.dev.Auction.application.AuctionService;
import com.programmers.dev.Auction.dto.AuctionSaveRequest;
import com.programmers.dev.Auction.dto.AuctionSaveResponse;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<AuctionSaveResponse> saveAuction(
        @RequestBody @Validated AuctionSaveRequest auctionSaveRequest,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CreamException(ErrorCode.INVALID_REQUEST_VALUE);
        }
        return ResponseEntity.ok(auctionService.save(auctionSaveRequest));
    }

}
