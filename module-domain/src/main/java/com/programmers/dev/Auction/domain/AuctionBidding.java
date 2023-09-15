package com.programmers.dev.Auction.domain;

import com.programmers.dev.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "AUCTION_BIDDINGS")
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

    public static AuctionBidding bidAuction(User user, Auction auction, Long price) {
        return new AuctionBidding(user, auction, price);
    }
}
