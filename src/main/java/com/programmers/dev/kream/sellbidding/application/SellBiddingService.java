package com.programmers.dev.kream.sellbidding.application;

import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.product.domain.SizedProduct;
import com.programmers.dev.kream.product.domain.SizedProductRepository;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBiddingRepository;
import com.programmers.dev.kream.sellbidding.domain.SellBidding;
import com.programmers.dev.kream.sellbidding.domain.SellBiddingRepository;
import com.programmers.dev.kream.sellbidding.ui.SellBiddingRequest;
import com.programmers.dev.kream.sellbidding.ui.SellBiddingResponse;
import com.programmers.dev.kream.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SellBiddingService {

    private final SellBiddingRepository sellBiddingRepository;
    private final PurchaseBiddingRepository purchaseBiddingRepository;
    private final SizedProductRepository sizedProductRepository;
    private final UserRepository userRepository;

    public SellBiddingService(SellBiddingRepository sellBiddingRepository, PurchaseBiddingRepository purchaseBiddingRepository, SizedProductRepository sizedProductRepository, UserRepository userRepository) {
        this.sellBiddingRepository = sellBiddingRepository;
        this.purchaseBiddingRepository = purchaseBiddingRepository;
        this.sizedProductRepository = sizedProductRepository;
        this.userRepository = userRepository;
    }


    /**
     * todo : 해당 비즈니스 예외 처리 구현
     * 판매입찰 등록 비즈니스 로직
     *
     * @throws IllegalArgumentException : 회원 id 및 사이즈가 있는 상품 id가 유효하지 않을 경우 예외 발생
      */
    @Transactional
    public SellBiddingResponse saveSellBidding(Long userId, Long sizedProductId, SellBiddingRequest sellBiddingRequest) {
        validateUserId(userId);
        SizedProduct sizedProduct = findSizedProduct(sizedProductId);

        SellBidding savedSellBidding = sellBiddingRepository.save(
                SellBidding.of(userId, sizedProduct.getId(), sellBiddingRequest)
        );

        return new SellBiddingResponse(savedSellBidding.getId());
    }

    private void validateUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("잘못된 회원 id 입니다.")
                );
    }

    private SizedProduct findSizedProduct(Long sizedProductId) {
        return sizedProductRepository.findById(sizedProductId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 상품이 존재하지 않습니다.")
                );
    }

    /**
     * 구매 입찰에 등록된 건 판매 비즈니스 로직
     *
     * @param userId : 판매자 id
     * @param purchaseBiddingId : 구매 입찰 id
     *
     * @throws IllegalStateException : 구매입찰 등록한 유저가 팔려고 하는 경우, 잘못된 id인 경우
     */
    @Transactional
    public SellBiddingResponse transactPurchaseBidding(Long userId, Long purchaseBiddingId) {
        validateUserId(userId);
        PurchaseBidding purchaseBidding = findPurchaseBidding(purchaseBiddingId);
        validateSellUserAndPurchaseUser(userId, purchaseBidding);
        SellBidding sellBidding = SellBidding.of(userId, purchaseBidding);
        purchaseBidding.changeStatus(Status.SHIPPED);
        sellBiddingRepository.save(sellBidding);

        return new SellBiddingResponse(sellBidding.getId());
    }

    private PurchaseBidding findPurchaseBidding(Long purchaseBiddingId) {
        return purchaseBiddingRepository.findById(purchaseBiddingId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 구매 입찰 정보가 없습니다.")
                );
    }

    private static void validateSellUserAndPurchaseUser(Long userId, PurchaseBidding purchaseBidding) {
        if (userId == purchaseBidding.getPurchaseBidderId()) {
            throw new IllegalArgumentException("비정상적인 접근입니다.");
        }
    }
}
