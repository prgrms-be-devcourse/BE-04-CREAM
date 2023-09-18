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
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BiddingService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BiddingRepository biddingRepository;

    @Transactional
    public BiddingResponse registerPurchaseBidding(Long userId, String storage, RegisterBiddingRequest request) {
        validateProductId(request);
        checkRequestPriceOverBiddingPrice(request, Bidding.BiddingType.SELL);

        Bidding savedBidding = biddingRepository.save(
                Bidding.registerPurchaseBidding(userId, request.productId(), request.price(), storage, request.dueDate())
        );

        return BiddingResponse.of(savedBidding.getId());
    }

    @Transactional
    public BiddingResponse transactSellBidding(Long userId, String storage, TransactBiddingRequest request) {
        validateBidding(userId,
                getBiddingByBiddingId(request.biddingId())
        );

        Bidding savedBidding = biddingRepository.save(
                Bidding.transactSellBidding(userId, storage, getBiddingByBiddingId(request.biddingId()))
        );

        return BiddingResponse.of(savedBidding.getId());
    }

    @Transactional
    public BiddingResponse registerSellBidding(Long userId, RegisterBiddingRequest request) {
        validateProductId(request);
        checkRequestPriceOverBiddingPrice(request, Bidding.BiddingType.PURCHASE);

        Bidding savedBidding = biddingRepository.save(
                Bidding.registerSellBidding(userId, request.productId(), request.price(), request.dueDate())
        );

        return BiddingResponse.of(savedBidding.getId());
    }

    @Transactional
    public BiddingResponse transactPurchaseBidding(Long userId, TransactBiddingRequest request) {
        Bidding purchaseBidding = getBiddingByBiddingId(request.biddingId());
        validateBidding(userId, purchaseBidding);

        Bidding savedBidding = biddingRepository.save(
                Bidding.transactPurchaseBidding(userId, purchaseBidding)
        );

        return BiddingResponse.of(savedBidding.getId());
    }

    @Transactional
    public void inspect(Long biddingId, String result) {
        getBiddingByBiddingId(biddingId).inspect(result);
    }

    @Transactional
    public void sendMoneyForBidding(Long userId, Long biddingId) {
        User user = getUserByUserId(userId);
        Bidding bidding = getBiddingByBiddingId(biddingId);
        bidding.checkBiddingBeforeDeposit(userId);
        checkBalance(user, bidding);
        sendMoneyForBidding(bidding, user);
    }

    private void checkBalance(User user, Bidding bidding) {
        if (user.getAccount() < bidding.getPrice()) {
            log.info("not enough money for pay. user account : {}", user.getAccount());
            throw new CreamException(ErrorCode.INSUFFICIENT_ACCOUNT_MONEY);
        }
    }

    private void sendMoneyForBidding(Bidding bidding, User user) {
        bidding.deposit();
        user.withdraw((long) bidding.getPrice());
    }

    @Transactional
    public void finish(Long userId, Long biddingId) {
        Bidding purchaseBidding = getBiddingByBiddingId(biddingId);
        finishPurchaseAndSellBidding(userId, purchaseBidding);
        depositMoneyAndPoint(userId, purchaseBidding);
    }

    private void finishPurchaseAndSellBidding(Long userId, Bidding bidding) {
        bidding.checkAuthorityOfUser(userId);
        bidding.finish();
        Bidding sellBidding = bidding.getBidding();
        sellBidding.finish();
    }

    private void depositMoneyAndPoint(Long userId, Bidding purchaseBidding) {
        User seller = getUserByUserId(purchaseBidding.getBidding().getUserId());
        seller.deposit((long) purchaseBidding.getPrice());
        seller.deposit((long) purchaseBidding.getPoint());
        User buyer = getUserByUserId(userId);
        buyer.deposit((long) purchaseBidding.getPoint());
    }

    private static void validateBidding(Long userId, Bidding sellBidding) {
        sellBidding.checkAbusing(userId);
        sellBidding.checkDurationOfBidding();
    }

    private User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> {
                    log.info("userId not exist in database, userId : {}", userId);
                    return new CreamException(ErrorCode.NO_AUTHENTICATION);
                }
        );
    }

    private Bidding getBiddingByBiddingId(Long biddingId) {
        return biddingRepository.findById(biddingId)
                .orElseThrow(
                        () -> {
                            log.info("biddingId not exist in database, biddingId : {}", biddingId);
                            return new CreamException(ErrorCode.INVALID_ID);
                        }
                );
    }

    private void validateProductId(RegisterBiddingRequest request) {
        productRepository.findById(request.productId()).orElseThrow(
                () -> {
                    log.info("invalid product Id : {}", request.productId());
                    return new CreamException(ErrorCode.INVALID_ID);
                }
        );
    }

    private void checkRequestPriceOverBiddingPrice(RegisterBiddingRequest request, Bidding.BiddingType biddingType) {
        biddingRepository.findSellBidding(request.productId(), Status.LIVE, biddingType).ifPresent(
                bidding -> {
                    if (bidding.getPrice() < request.price()) {
                        log.info("bidding price : {}, request price : {}", bidding.getPrice(), request.price());
                        throw new CreamException(ErrorCode.OVER_PRICE);
                    }
                }
        );
    }
}
