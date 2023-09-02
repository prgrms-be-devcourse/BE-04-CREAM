package com.programmers.dev.kream.purchasebidding.application;


import com.programmers.dev.kream.common.application.BankService;
import com.programmers.dev.kream.common.bidding.BiddingDuration;
import com.programmers.dev.kream.common.bidding.Status;
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
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다. " + request.sizedProductId()));
        User purchaser = userRepository.findById(purchaserId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다. " + purchaserId));
        User seller = userRepository.findById(sellBidding.getSellBidderId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다. " + sellBidding.getSellBidderId()));

        /*
            즉시 입찰 구매
            -구매자: 바로 계좌에서 결제된 가격만큼 인출
            -판매자: 판매자가 상품을 발송한 후 검수 합격이 완료가 된 다음날 입금
                -> 1차 스프린트에서는 배송과정 생략 후 검수 합격부터 시작하는 시나리오
                   구매자, 판매자 계좌에 바로 거래가 반영되도록 구현함.
         */
        sellBidding.changeStatus(Status.AUTHENTICATED);
        bankService.accountTransaction(purchaser, seller, sellBidding.getPrice());

        return purchaseBiddingRepository.save(
                new PurchaseBidding(purchaserId, request.sizedProductId(), request.price(), Status.AUTHENTICATED)).getId();
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
