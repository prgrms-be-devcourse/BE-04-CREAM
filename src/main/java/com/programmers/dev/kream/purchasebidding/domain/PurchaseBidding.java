package com.programmers.dev.kream.purchasebidding.domain;

import com.programmers.dev.kream.common.bidding.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PURCHASE_BIDDINGS")
public class PurchaseBidding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PURCHASE_BIDDER_ID", nullable = false)
    private Long purchaseBidderId;

    @Column(name = "SIZED_PRODUCT_ID", nullable = false)
    private Long sizedProductId;

    @Column(name = "PRICE", nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    @Column(name = "START_DATE", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDateTime dueDate;

    protected PurchaseBidding() { }

    public Long getId() {
        return id;
    }

    public Long getPurchaseBidderId() {
        return purchaseBidderId;
    }

    public Long getSizedProductId() {
        return sizedProductId;
    }

    public Long getPrice() {
        return price;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }
}
