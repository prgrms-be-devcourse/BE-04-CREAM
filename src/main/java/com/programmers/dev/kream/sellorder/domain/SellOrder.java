package com.programmers.dev.kream.sellorder.domain;

import com.programmers.dev.kream.common.bidding.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SELL_ORDERS")
public class SellOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SELLER_ID", nullable = false)
    private Long sellerId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "PRICE", nullable = false)
    private Integer price;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    // 보관 시작 날짜
    @Column(name = "STORAGE_START_DATE", updatable = false)
    private LocalDateTime storageStartDate;

    // 거래 체결 날짜
    @Column(name = "TRANSACTION_DATE")
    private LocalDateTime transactionDate;

    protected SellOrder() {

    }

    public SellOrder(Long sellerId, Long productId, Integer price, Status status, LocalDateTime storageStartDate, LocalDateTime transactionDate) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.price = price;
        this.status = status;
        this.storageStartDate = storageStartDate;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getPrice() {
        return price;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getStorageStartDate() {
        return storageStartDate;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}
