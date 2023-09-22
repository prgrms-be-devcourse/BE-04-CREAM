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

        Bidding savedPurchaseBidding = biddingRepository.save(
                Bidding.registerPurchaseBidding(userId, request.productId(), request.price(), storage, request.dueDate())
        );

        return BiddingResponse.of(savedPurchaseBidding.getId());
    }

    @Transactional
    public BiddingResponse transactSellBidding(Long userId, String storage, TransactBiddingRequest request) {
        Bidding sellBidding = getBiddingByBiddingId(request.biddingId());
        validateBidding(userId,sellBidding);

        Bidding savedPurchaseBidding = biddingRepository.save(
                Bidding.transactSellBidding(userId, storage, sellBidding)
        );
        sellBidding.relateBidding(savedPurchaseBidding);

        return BiddingResponse.of(savedPurchaseBidding.getId());
    }

    @Transactional
    public BiddingResponse registerSellBidding(Long userId, RegisterBiddingRequest request) {
        validateProductId(request);
        checkRequestPriceUnderBiddingPrice(request, Bidding.BiddingType.PURCHASE);

        Bidding savedSellBidding = biddingRepository.save(
                Bidding.registerSellBidding(userId, request.productId(), request.price(), request.dueDate())
        );

        return BiddingResponse.of(savedSellBidding.getId());
    }

    @Transactional
    public BiddingResponse transactPurchaseBidding(Long userId, TransactBiddingRequest request) {
        Bidding purchaseBidding = getBiddingByBiddingId(request.biddingId());
        validateBidding(userId, purchaseBidding);

        Bidding savedBidding = biddingRepository.save(
                Bidding.transactPurchaseBidding(userId, purchaseBidding)
        );

        purchaseBidding.relateBidding(savedBidding);
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
        finishPurchaseBidding(userId, purchaseBidding);
        finishSellBidding(purchaseBidding);
        depositMoneyAndPoint(userId, purchaseBidding);
    }

    private void finishPurchaseBidding(Long userId, Bidding purchaseBidding) {
        purchaseBidding.checkAuthorityOfUser(userId);
        purchaseBidding.checkStatusDeposit();
        purchaseBidding.finishPurchaseBidding();
    }

    private void finishSellBidding(Bidding purchaseBidding) {
        Bidding sellBidding = purchaseBidding.getBidding();
        sellBidding.finishSellBidding();
    }

    private void depositMoneyAndPoint(Long userId, Bidding purchaseBidding) {
        User seller = getUserByUserId(purchaseBidding.getBidding().getUserId());
        depositBiddingMoney(purchaseBidding, seller);
        depositPoint(seller, purchaseBidding.getPoint());
        User buyer = getUserByUserId(userId);
        depositPoint(buyer, purchaseBidding.getPoint());
    }

    private void depositBiddingMoney(Bidding purchaseBidding, User seller) {
        seller.deposit((long) purchaseBidding.getPrice());
    }

    private void depositPoint(User user, int point) {
        user.deposit((long) point);
    }

    @Transactional
    public void cancel(Long userId, Long biddingId) {
        Bidding bidding = getBiddingByBiddingId(biddingId);
        bidding.checkAuthorityOfUser(userId);
        int penalty = bidding.cancel();
        User user = getUserByUserId(userId);
        try {
            user.withdraw((long) penalty);
        } catch (CreamException e) {
            log.warn("bidding cancel error", e);
            user.withdrawInsufficientMoney((long) penalty);
        }
        if (bidding.getBidding() != null) {
            User biddingOpponent = getUserByUserId(bidding.getBidding().getUserId());
            biddingOpponent.deposit((long) penalty);
        }
    }

    private void validateBidding(Long userId, Bidding toValidate) {
        toValidate.checkStatusLive();
        toValidate.checkAbusing(userId);
        toValidate.checkDurationOfBidding();
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

    private void checkRequestPriceUnderBiddingPrice(RegisterBiddingRequest request, Bidding.BiddingType biddingType) {
        biddingRepository.findSellBidding(request.productId(), Status.LIVE, biddingType).ifPresent(
                bidding -> {
                    if (bidding.getPrice() > request.price()) {
                        log.info("bidding price : {}, request price : {}", bidding.getPrice(), request.price());
                        throw new CreamException(ErrorCode.OVER_PRICE);
                    }
                }
        );
    }
}
