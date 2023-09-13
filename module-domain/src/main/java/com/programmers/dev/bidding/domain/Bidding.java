package com.programmers.dev.bidding.domain;

import com.programmers.dev.common.Status;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "BIDDINGS")
public class Bidding {

    public enum BiddingType {
        SELL, PURCHASE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "PRICE", nullable = false)
    private int price;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Enumerated(value = EnumType.STRING)
    private BiddingType biddingType;

    @Column(name = "START_DATE", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "TRANSACTION_DATE")
    private LocalDateTime transactionDate;

    protected Bidding() {

    }

    private Bidding(Long userId, Long productId, int price, BiddingType biddingType, Long dueDate) {
        this.userId = userId;
        this.productId = productId;
        this.price = price;
        this.status = Status.LIVE;
        this.biddingType = biddingType;
        this.startDate = LocalDateTime.now();
        this.dueDate = this.startDate.plusDays(dueDate);
    }

    public static Bidding registerPurchaseBidding(Long userId, Long productId, Integer price, Long dueDate) {
        return new Bidding(userId, productId, price, BiddingType.PURCHASE, dueDate);
    }
}
