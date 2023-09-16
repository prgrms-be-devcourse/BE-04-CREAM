package com.programmers.dev.Auction.application;

import com.programmers.dev.Auction.domain.Auction;
import com.programmers.dev.Auction.domain.AuctionBidding;
import com.programmers.dev.Auction.domain.AuctionBiddingRepository;
import com.programmers.dev.Auction.domain.AuctionRepository;
import com.programmers.dev.Auction.dto.*;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.product.domain.Product;
import com.programmers.dev.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.programmers.dev.exception.ErrorCode.INVALID_ID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionService {

    private final AuctionRepository auctionRepository;

    private final ProductRepository productRepository;

    private final AuctionBiddingRepository auctionBiddingRepository;

    @Transactional
    public AuctionSaveResponse save(AuctionSaveRequest auctionSaveRequest) {
        Product product = findProductById(auctionSaveRequest.productId());

        Auction auction = Auction.createAuctionFirst(product, auctionSaveRequest);

        return new AuctionSaveResponse(auctionRepository.save(auction).getId());

    }

    @Transactional
    public AuctionStatusChangeResponse changeAuctionStatus(AuctionStatusChangeRequest auctionStatusChangeRequest) {
        Auction auction = findAuctionById(auctionStatusChangeRequest.id());
        auction.changeStatus(auctionStatusChangeRequest.auctionStatus());

        if (auctionStatusChangeRequest.auctionStatus() == AuctionStatus.FINISHED) {
            registerSuccessfulBidder(auctionStatusChangeRequest);
        }

        return new AuctionStatusChangeResponse(auction.getId(), auction.getAuctionStatus());
    }

    private void registerSuccessfulBidder(AuctionStatusChangeRequest auctionStatusChangeRequest) {
        AuctionBidding auctionBidding = findHighestBidPrice(auctionStatusChangeRequest.id());

        Long successfulBidderId = auctionBidding.getUser().getId();
        Long successfulBidPrice = auctionBidding.getPrice();

        auctionBidding.getAuction()
            .registerSuccessfulBidder(successfulBidderId, successfulBidPrice);
    }

    private AuctionBidding findHighestBidPrice(Long auctionId) {
        return auctionBiddingRepository.findTopByAuctionIdOrderByPriceDesc(auctionId)
            .orElseThrow(() -> new CreamException(INVALID_ID));
    }

    private Auction findAuctionById(Long id) {
        return auctionRepository.findById(id)
            .orElseThrow(() -> new CreamException(INVALID_ID));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CreamException(INVALID_ID));
    }
}
