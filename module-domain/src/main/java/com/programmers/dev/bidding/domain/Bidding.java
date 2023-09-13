package com.programmers.dev.bidding.domain;

import com.programmers.dev.common.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BIDDINGS")
public class Bidding {

    enum BiddingType {
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

}
