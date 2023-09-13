package com.programmers.dev.Auction.application;

import com.programmers.dev.Auction.domain.Auction;
import com.programmers.dev.Auction.domain.AuctionRepository;
import com.programmers.dev.Auction.dto.AuctionSaveRequest;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.exception.CreamException;
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

    @Transactional
    public Long save(AuctionSaveRequest auctionSaveRequest) {
        Product product = findProductById(auctionSaveRequest.productId());

        Auction auction = new Auction(
            product,
            auctionSaveRequest.startPrice(),
            auctionSaveRequest.startTime(),
            auctionSaveRequest.endTime());

        return auctionRepository.save(auction).getId();
    }

    @Transactional
    public void startAuction(Long id) {
        Auction auction = findAuctionById(id);
        auction.changeStatus(AuctionStatus.ONGOING);
    }

    @Transactional
    public void finishAuction(Long id) {
        Auction auction = findAuctionById(id);
        auction.changeStatus(AuctionStatus.FINISHED);
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
