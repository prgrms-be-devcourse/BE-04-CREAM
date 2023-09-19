package com.programmers.dev.Auction.application;

import com.programmers.dev.Auction.domain.Auction;
import com.programmers.dev.Auction.domain.AuctionBidding;
import com.programmers.dev.Auction.domain.AuctionBiddingRepository;
import com.programmers.dev.Auction.domain.AuctionRepository;
import com.programmers.dev.Auction.dto.*;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.user.application.UserFindService;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.programmers.dev.exception.ErrorCode.INVALID_CANCEL_BIDDING;
import static com.programmers.dev.exception.ErrorCode.INVALID_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuctionBiddingService {

    private final AuctionBiddingRepository auctionBiddingRepository;

    private final UserRepository userRepository;

    private final AuctionRepository auctionRepository;

    // TODO 매일 자정 경매 입찰 테이블 정리

    @Transactional
    public AuctionBidResponse bidAuction(Long userId, AuctionBidRequest auctionBidRequest) {
        Auction auction = findAuctionById(auctionBidRequest.auctionId());

        auction.validateAuctionBiddingTime();

        User user = findUserById(userId);

        Long topBiddingPrice = getTopBiddingPrice(BiddingPriceGetRequest.of(auctionBidRequest.auctionId()));

        AuctionBidding auctionBidding = AuctionBidding.bidAuction(user, auction, auctionBidRequest.price(), topBiddingPrice);
        AuctionBidding savedAuctionBidding = auctionBiddingRepository.save(auctionBidding);

        return AuctionBidResponse.fromEntity(savedAuctionBidding);
    }

    @Transactional
    public void cancelAuctionBid(Long userId, AuctionBiddingCancelRequest request) {
        AuctionBidding auctionBidding = getCancelAuctionBidding(userId, request);

        auctionBiddingRepository.delete(auctionBidding);
    }

    @Transactional
    public BidderDecisionResponse decidePurchaseStatus(Long userId, BidderDecisionRequest request) {
        if (request.purchaseStatus()) {
            Auction auction = findAuctionById(request.auctionId());
            auction.registerSuccessfulBidder(userId, request.price());
            auction.changeStatus(AuctionStatus.FINISHED);

            return BidderDecisionResponse.of(userId, true, request.price());
        }
        auctionBiddingRepository.deleteLastAuctionBidding(userId, request.auctionId(), request.price());

        return BidderDecisionResponse.of(userId, false, request.price());
    }

    private AuctionBidding getCancelAuctionBidding(Long userId, AuctionBiddingCancelRequest request) {
        return auctionBiddingRepository.findCancelBidding(userId, request.auctionId(), request.price())
            .orElseThrow(() -> {
                log.info("[getCancelAuctionBidding] 취소할 경매 입찰을 조회하는 과정에서 예외가 발생하였습니다.");
                return new CreamException(INVALID_CANCEL_BIDDING);
            });
    }

    public BiddingPriceGetResponse getCurrentBiddingPrice(BiddingPriceGetRequest request) {
        Long topBiddingPrice = getTopBiddingPrice(request);

        return BiddingPriceGetResponse.of(request.auctionId(), topBiddingPrice);
    }

    private Long getTopBiddingPrice(BiddingPriceGetRequest request) {
        return auctionBiddingRepository.findTopBiddingPrice(request.auctionId())
            .map(AuctionBidding::getPrice)
            .orElse(getStartPrice(request));
    }

    private Long getStartPrice(BiddingPriceGetRequest request) {
        return auctionRepository.findById(request.auctionId())
            .map(Auction::getStartPrice)
            .orElseThrow(() -> {
                log.info("[getStartPrice] 해당 auctionId({})에 대한 경매는 존재하지 않습니다.", request.auctionId());
                return new CreamException(INVALID_ID);
            });
    }

    private Auction findAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId)
            .orElseThrow(() -> {
                log.info("[findAuctionById] 해당 auctionId({})에 대한 경매는 존재하지 않습니다.", auctionId);
                return new CreamException(INVALID_ID);
            });
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> {
                log.info("[findUserById] 해당 userId({})에 대한 사용자는 존재하지 않습니다.", userId);
                return new CreamException(INVALID_ID);
            });
    }
}
