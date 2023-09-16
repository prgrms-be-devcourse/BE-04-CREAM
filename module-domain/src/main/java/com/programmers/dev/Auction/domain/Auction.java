package com.programmers.dev.Auction.domain;

import com.programmers.dev.Auction.dto.AuctionSaveRequest;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.programmers.dev.exception.ErrorCode.*;

@Entity
@Table(name = "AUCTIONS")
@Getter
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Column(name = "START_PRICE", nullable = false)
    private Long startPrice;

    @Column(name = "START_TIME", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "END_TIME", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "AUCTION_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuctionStatus auctionStatus;

    @Column(name = "BIDDER_ID")
    private Long bidderId;

    @Column(name = "PRICE")
    private Long price;

    protected Auction() {

    }

    public static Auction createAuctionFirst(Product product, AuctionSaveRequest auctionSaveRequest) {
        return new Auction(
            product,
            auctionSaveRequest.startPrice(),
            auctionSaveRequest.startTime(),
            auctionSaveRequest.endTime(),
            AuctionStatus.BEFORE,
            null, null);
    }

    private Auction(Product product, Long startPrice, LocalDateTime startTime, LocalDateTime endTime, AuctionStatus auctionStatus, Long bidderId, Long price) {
        this.product = product;
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.auctionStatus = auctionStatus;
        this.bidderId = bidderId;
        this.price = price;
    }

    public void changeStatus(AuctionStatus auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public void validateAuctionBiddingTime() {
        if (this.auctionStatus != AuctionStatus.ONGOING) {
            throw new CreamException(INVALID_AUCTION_BIDDING);
        }
    }

    public void registerSuccessfulBidder(Long bidderId, Long price) {
        this.bidderId = bidderId;
        this.price = price;
    }
}
