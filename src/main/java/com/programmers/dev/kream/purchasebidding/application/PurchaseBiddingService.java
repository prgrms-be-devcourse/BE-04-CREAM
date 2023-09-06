package com.programmers.dev.kream.purchasebidding.application;


import com.programmers.dev.kream.common.application.BankService;
import com.programmers.dev.kream.common.bidding.BiddingDuration;
import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.exception.CreamException;
import com.programmers.dev.kream.exception.ErrorCode;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBiddingRepository;
import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseBiddingBidRequest;
import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseBiddingNowRequest;
import com.programmers.dev.kream.sellbidding.domain.SellBidding;
import com.programmers.dev.kream.sellbidding.domain.SellBiddingRepository;
import com.programmers.dev.kream.user.domain.User;
import com.programmers.dev.kream.user.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional(readOnly = true)
public class PurchaseBiddingService {

    private final PurchaseBiddingRepository purchaseBiddingRepository;

    private final SellBiddingRepository sellBiddingRepository;

    private final UserRepository userRepository;

    private final BankService bankService;

    public PurchaseBiddingService(PurchaseBiddingRepository purchaseBiddingRepository, SellBiddingRepository sellBiddingRepository, UserRepository userRepository, BankService bankService) {
        this.purchaseBiddingRepository = purchaseBiddingRepository;
        this.sellBiddingRepository = sellBiddingRepository;
        this.userRepository = userRepository;
        this.bankService = bankService;
    }

    public PurchaseBidding findById(Long id) {
        return purchaseBiddingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 입찰입니다. " + id));
    }

    @Transactional
    public Long purchaseNow(Long purchaserId, PurchaseBiddingNowRequest request) {
        SellBidding sellBidding = sellBiddingRepository.findLowPriceBidding(request.price(), request.sizedProductId())
                .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));

        User purchaser = userRepository.findById(purchaserId)
                .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));

        User seller = userRepository.findById(sellBidding.getSellBidderId())
                .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));

        sellBidding.changeStatus(Status.SHIPPED);
        bankService.accountTransaction(purchaser, seller, sellBidding.getPrice());

        return purchaseBiddingRepository.save(
                new PurchaseBidding(purchaserId, request.sizedProductId(), request.price(), Status.SHIPPED)).getId();
    }

    @Transactional
    public Long bid(Long purchaserId, PurchaseBiddingBidRequest request) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = addDays(startDate, request.biddingDuration());

        return purchaseBiddingRepository.save(
                new PurchaseBidding(purchaserId, request.sizedProductId(), request.price(), Status.LIVE, startDate, dueDate)).getId();
    }

    private LocalDateTime addDays(LocalDateTime now, BiddingDuration biddingDuration) {
        return now.plusDays(biddingDuration.getDays());
    }
}
