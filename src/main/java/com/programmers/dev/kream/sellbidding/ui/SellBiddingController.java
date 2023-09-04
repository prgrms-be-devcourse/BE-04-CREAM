package com.programmers.dev.kream.sellbidding.ui;

import com.programmers.dev.kream.sellbidding.application.SellBiddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 판매 입찰 관련 Rest Controller
 */
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


    /**
     * 판매 입찰 등록 API
     * todo : 추후에 로그인 기능을 사용할 경우 Cookie로 값을 받아 올 수 있도록 구현
     *
     * @param userId : 회원 아이디
     * @param sizedProductId : 상품 아이디
     * @param sellBiddingRequest : 판매입찰과 관련된 정보
     *
     * @see SellBiddingRequest
     * @return
     */
    @PostMapping

    public ResponseEntity<SellBiddingResponse> saveSellBidding(
            @RequestParam Long userId,
            @RequestParam Long sizedProductId,
            @RequestBody SellBiddingRequest sellBiddingRequest) {

        return ResponseEntity.ok(
                sellBiddingService.saveSellBidding(userId, sizedProductId, sellBiddingRequest)
        );
    }
}
