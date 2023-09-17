package com.programmers.dev.Auction.domain;

import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "AUCTION_BIDDINGS",
indexes = @Index(name = "IDX_AUCTION_ID_PRICE", columnList = "AUCTION_ID ASC, PRICE DESC"))
@Getter
public class AuctionBidding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUCTION_ID")
    private Auction auction;

    @Column(name = "PRICE")
    private Long price;

    protected AuctionBidding() {

    }

    public AuctionBidding(User user, Auction auction, Long price) {
        this.user = user;
        this.auction = auction;
        this.price = price;
    }

    public static AuctionBidding bidAuction(User user, Auction auction, Long price, Long topBiddingPrice) {
        validateTopBiddingPrice(price, topBiddingPrice);

        return new AuctionBidding(user, auction, price);
    }

    private static void validateTopBiddingPrice(Long price, Long topBiddingPrice) {
        if (price <= topBiddingPrice) {
            throw new CreamException(ErrorCode.INVALID_BIDDING_PRICE);
        }
    }
}
