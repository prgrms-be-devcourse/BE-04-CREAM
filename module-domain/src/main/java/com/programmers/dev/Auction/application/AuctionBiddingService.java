package com.programmers.dev.Auction.application;

import com.programmers.dev.Auction.domain.Auction;
import com.programmers.dev.Auction.domain.AuctionBidding;
import com.programmers.dev.Auction.domain.AuctionBiddingRepository;
import com.programmers.dev.Auction.domain.AuctionRepository;
import com.programmers.dev.Auction.dto.AuctionBidRequest;
import com.programmers.dev.Auction.dto.AuctionBidResponse;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.programmers.dev.exception.ErrorCode.INVALID_AUCTION_BIDDING;
import static com.programmers.dev.exception.ErrorCode.INVALID_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionBiddingService {

    private final AuctionBiddingRepository auctionBiddingRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    @Transactional
    public AuctionBidResponse bidAuction(AuctionBidRequest auctionBidRequest) {
        Auction auction = findAuctionById(auctionBidRequest.auctionId());

        validateAuctionBiddingTime(auction);

        User user = findUserById(auctionBidRequest.userId());

        AuctionBidding auctionBidding = new AuctionBidding(user, auction, auctionBidRequest.price());
        AuctionBidding savedAuctionBidding = auctionBiddingRepository.save(auctionBidding);

        return AuctionBidResponse.fromEntity(savedAuctionBidding);
    }

    private static void validateAuctionBiddingTime(Auction auction) {
        if (auction.getAuctionStatus() != AuctionStatus.ONGOING) {
            throw new CreamException(INVALID_AUCTION_BIDDING);
        }
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
