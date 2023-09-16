package com.programmers.dev.Auction.application;

import com.programmers.dev.Auction.domain.Auction;
import com.programmers.dev.Auction.domain.AuctionBidding;
import com.programmers.dev.Auction.domain.AuctionBiddingRepository;
import com.programmers.dev.Auction.domain.AuctionRepository;
import com.programmers.dev.Auction.dto.*;
import com.programmers.dev.exception.CreamException;
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

    // TODO 현재 입찰 최고가보다 낮은 금액은 입찰 불가하게끔 고도화 필요.
    @Transactional
    public AuctionBidResponse bidAuction(Long userId, AuctionBidRequest auctionBidRequest) {
        Auction auction = findAuctionById(auctionBidRequest.auctionId());

        auction.validateAuctionBiddingTime();

        User user = findUserById(userId);

        AuctionBidding auctionBidding = AuctionBidding.bidAuction(user, auction, auctionBidRequest.price());
        AuctionBidding savedAuctionBidding = auctionBiddingRepository.save(auctionBidding);

        return AuctionBidResponse.fromEntity(savedAuctionBidding);
    }

    @Transactional
    public void cancelAuctionBid(Long userId, AuctionBiddingCancelRequest request) {
        auctionBiddingRepository.deleteLastAuctionBidding(userId, request.auctionId(), request.price());
    }

    // TODO 인덱스 활용
    public BiddingPriceGetResponse getCurrentBiddingPrice(BiddingPriceGetRequest request) {
        AuctionBidding auctionBidding = getTopBiddingPrice(request);

        return BiddingPriceGetResponse.of(request.auctionId(), auctionBidding.getPrice());
    }

    private AuctionBidding getTopBiddingPrice(BiddingPriceGetRequest request) {
        return auctionBiddingRepository.findTopByAuctionIdOrderByPriceDesc(request.auctionId())
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
