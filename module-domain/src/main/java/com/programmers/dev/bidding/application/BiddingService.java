package com.programmers.dev.bidding.application;

import com.programmers.dev.bidding.domain.Bidding;
import com.programmers.dev.bidding.domain.BiddingRepository;
import com.programmers.dev.bidding.dto.BiddingResponse;
import com.programmers.dev.bidding.dto.RegisterBiddingRequest;
import com.programmers.dev.bidding.dto.TransactBiddingRequest;
import com.programmers.dev.common.Status;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.product.domain.ProductRepository;
import com.programmers.dev.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BiddingService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BiddingRepository biddingRepository;

    @Transactional
    public BiddingResponse registerPurchaseBidding(Long userId, RegisterBiddingRequest request) {
        validateUserId(userId);
        validateProductId(request);
        checkRequestPriceOverBiddingPrice(request, Bidding.BiddingType.SELL);

        Bidding bidding = Bidding.registerPurchaseBidding(userId, request.productId(), request.price(), request.dueDate());
        Bidding savedBidding = biddingRepository.save(bidding);

        return BiddingResponse.of(savedBidding.getId());
    }

    @Transactional
    public BiddingResponse transactSellBidding(Long userId, TransactBiddingRequest request) {
        validateUserId(userId);
        Bidding sellBidding = getBidding(request.biddingId());
        validateBadRequest(userId, sellBidding);
        sellBidding.checkAfterDueDate();
        Bidding bidding = Bidding.transactSellBidding(userId, sellBidding);
        Bidding savedBidding = biddingRepository.save(bidding);

        return BiddingResponse.of(savedBidding.getId());
    }

    @Transactional
    public BiddingResponse registerSellBidding(Long userId, RegisterBiddingRequest request) {
        validateUserId(userId);
        validateProductId(request);
        checkRequestPriceOverBiddingPrice(request, Bidding.BiddingType.PURCHASE);
        Bidding bidding = Bidding.registerSellBidding(userId, request.productId(), request.price(), request.dueDate());
        Bidding savedBidding = biddingRepository.save(bidding);

        return BiddingResponse.of(savedBidding.getId());
    }

    private void checkRequestPriceOverBiddingPrice(RegisterBiddingRequest request, Bidding.BiddingType biddingType) {
        biddingRepository.findSellBidding(request.productId(), Status.LIVE, biddingType)
                .stream().sorted(Comparator.comparingInt(Bidding::getPrice))
                .findFirst().ifPresent(
                        bidding -> {
                            if (bidding.getPrice() < request.price()) {
                                throw new CreamException(ErrorCode.OVER_PRICE);
                            }
                        }
                );
    }

    @Transactional
    public BiddingResponse transactPurchaseBidding(Long userId, TransactBiddingRequest request) {
        validateUserId(userId);
        Bidding purchaseBidding = getBidding(request.biddingId());
        validateBadRequest(userId, purchaseBidding);
        purchaseBidding.checkAfterDueDate();
        Bidding bidding = Bidding.transactPurchaseBidding(userId, purchaseBidding);
        Bidding savedBidding = biddingRepository.save(bidding);

        return BiddingResponse.of(savedBidding.getId());
    }

    private void validateUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new CreamException(ErrorCode.NO_AUTHENTICATION)
        );
    }

    private Bidding getBidding(Long biddingId) {
        return biddingRepository.findById(biddingId)
                .orElseThrow(
                        () -> new CreamException(ErrorCode.INVALID_ID)
                );
    }

    private void validateProductId(RegisterBiddingRequest request) {
        productRepository.findById(request.productId()).orElseThrow(
                () -> new CreamException(ErrorCode.INVALID_ID)
        );
    }

    private void validateBadRequest(Long userId, Bidding bidding) {
        if (bidding.getUserId().equals(userId)) {
            throw new CreamException(ErrorCode.BAD_BUSINESS_LOGIC);
        }
    }

}