package com.programmers.dev.Auction.application;

import com.programmers.dev.Auction.domain.Auction;
import com.programmers.dev.Auction.domain.AuctionBidding;
import com.programmers.dev.Auction.domain.AuctionBiddingRepository;
import com.programmers.dev.Auction.domain.AuctionRepository;
import com.programmers.dev.Auction.dto.*;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.programmers.dev.exception.ErrorCode.INVALID_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
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
        auctionBiddingRepository.deleteLastAuctionBidding(userId, request.auctionId(), request.price());
    }

    // TODO 인덱스 활용
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
            .orElseThrow(() -> new CreamException(INVALID_ID));
    }

    private Auction findAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId)
            .orElseThrow(() -> new CreamException(INVALID_ID));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CreamException(INVALID_ID));
    }
}
