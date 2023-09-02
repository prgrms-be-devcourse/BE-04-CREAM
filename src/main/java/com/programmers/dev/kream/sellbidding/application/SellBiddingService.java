package com.programmers.dev.kream.sellbidding.application;

import com.programmers.dev.kream.product.domain.SizedProduct;
import com.programmers.dev.kream.product.domain.SizedProductRepository;
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
    private final SizedProductRepository sizedProductRepository;
    private final UserRepository userRepository;

    public SellBiddingService(SellBiddingRepository sellBiddingRepository, SizedProductRepository sizedProductRepository, UserRepository userRepository) {
        this.sellBiddingRepository = sellBiddingRepository;
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

}
