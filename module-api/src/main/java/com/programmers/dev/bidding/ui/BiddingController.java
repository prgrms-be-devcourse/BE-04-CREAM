package com.programmers.dev.bidding.ui;

import com.programmers.dev.bidding.application.BiddingService;
import com.programmers.dev.bidding.dto.BiddingResponse;
import com.programmers.dev.bidding.dto.RegisterPurchaseBiddingRequest;
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
            @RequestBody @Valid RegisterPurchaseBiddingRequest request
    ) {
        BiddingResponse biddingResponse = biddingService.registerPurchaseBidding(userId, request);
        return ResponseEntity.created(URI.create(url + "/purchase")).body(biddingResponse);
    }

}
