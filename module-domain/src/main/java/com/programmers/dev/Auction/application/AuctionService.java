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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.programmers.dev.exception.ErrorCode.INVALID_ID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;

    private final ProductRepository productRepository;

    private final AuctionBiddingRepository auctionBiddingRepository;

    @Transactional
    public AuctionSaveResponse save(AuctionSaveRequest auctionSaveRequest) {
        Product product = findProductById(auctionSaveRequest.productId());

        Auction auction = Auction.createAuctionFirst(product, auctionSaveRequest);

        return AuctionSaveResponse.of(auctionRepository.save(auction).getId());

    }

    @Transactional
    public AuctionStatusChangeResponse changeAuctionStatus(AuctionStatusChangeRequest auctionStatusChangeRequest) {
        validateStatusBefore(auctionStatusChangeRequest);

        Auction auction = findAuctionById(auctionStatusChangeRequest.id());
        auction.changeStatus(auctionStatusChangeRequest.auctionStatus());

        if (auctionStatusChangeRequest.auctionStatus() == AuctionStatus.FINISHED) {
            registerSuccessfulBidder(auctionStatusChangeRequest);
        }

        return AuctionStatusChangeResponse.of(auction.getId(), auction.getAuctionStatus());
    }

    public SuccessfulBidderGetResponse findSuccessfulBidder(SuccessfulBidderGetRequest request) {
        Auction auction = findAuctionById(request.auctionId());

        auction.checkFinishedAuction();

        return SuccessfulBidderGetResponse.of(request.auctionId(), auction.getBidderId(), auction.getPrice());
    }

    private void validateStatusBefore(AuctionStatusChangeRequest auctionStatusChangeRequest) {
        if (auctionStatusChangeRequest.auctionStatus() == AuctionStatus.BEFORE) {
            log.info("[validateStatusBefore] 진행중이거나 종료된 경매의 상태를 경매전 상태로 변경할 수 없습니다.");
            throw new CreamException(ErrorCode.INVALID_CHANGE_STATUS);
        }
    }

    //TODO 경매에 대한 입찰자가 없을 경우

    private void registerSuccessfulBidder(AuctionStatusChangeRequest auctionStatusChangeRequest) {
        AuctionBidding auctionBidding = findHighestBidPrice(auctionStatusChangeRequest.id());

        Long successfulBidderId = auctionBidding.getUser().getId();
        Long successfulBidPrice = auctionBidding.getPrice();

        auctionBidding.getAuction()
            .registerSuccessfulBidder(successfulBidderId, successfulBidPrice);
    }

    private AuctionBidding findHighestBidPrice(Long auctionId) {
        return auctionBiddingRepository.findTopBiddingPrice(auctionId)
            .orElseThrow(() -> {
                log.info("[findHighestBidPrice] 해당 auctionId({})에 대한 입찰 내역은 존재하지 않습니다.", auctionId);
                return new CreamException(INVALID_ID);
            });

    }

    private Auction findAuctionById(Long id) {
        return auctionRepository.findById(id)
            .orElseThrow(() -> {
                log.info("[findAuctionById] 해당 auctionId({})에 대한 경매는 존재하지 않습니다.", id);
                return new CreamException(INVALID_ID);
            });
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> {
                log.info("[findProductById] 해당 productId({})에 대한 상품은 존재하지 않습니다.", productId);
                return new CreamException(INVALID_ID);
            });
    }
}
