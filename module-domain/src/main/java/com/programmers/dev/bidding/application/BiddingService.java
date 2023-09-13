package com.programmers.dev.bidding.application;

import com.programmers.dev.bidding.domain.Bidding;
import com.programmers.dev.bidding.domain.BiddingRepository;
import com.programmers.dev.bidding.dto.BiddingResponse;
import com.programmers.dev.bidding.dto.RegisterPurchaseBiddingRequest;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.product.domain.ProductRepository;
import com.programmers.dev.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BiddingService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BiddingRepository biddingRepository;

    @Transactional
    public BiddingResponse registerPurchaseBidding(Long userId, RegisterPurchaseBiddingRequest request) {
        validateUserId(userId);
        validateProductId(request);
        Bidding bidding = Bidding.registerPurchaseBidding(userId, request.productId(), request.price(), request.dueDate());
        Bidding savedBidding = biddingRepository.save(bidding);

        return BiddingResponse.of(savedBidding.getId());
    }

    private void validateUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new CreamException(ErrorCode.NO_AUTHENTICATION)
        );
    }

    private void validateProductId(RegisterPurchaseBiddingRequest request) {
        productRepository.findById(request.productId()).orElseThrow(
                () -> new CreamException(ErrorCode.INVALID_ID)
        );
    }
}
