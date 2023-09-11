package com.programmers.dev.kream.purchaseorder.domain;

import com.programmers.dev.kream.common.bidding.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PURCHASE_ORDERS")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PURCHASE_ORDERER_ID", nullable = false)
    private Long purchaseOrdererId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "PRICE", nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    @Column(name = "TRANSACTION_DATE", nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    protected PurchaseOrder() { }

    public PurchaseOrder(Long purchaseOrdererId, Long productId, Long price, Status status, LocalDateTime transactDate) {
        this.purchaseOrdererId = purchaseOrdererId;
        this.productId = productId;
        this.price = price;
        this.status = status;
        this.transactDate = transactDate;
    }

    public Long getId() {
        return id;
    }

    public Long getPurchaseOrdererId() {
        return purchaseOrdererId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getPrice() {
        return price;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getTransactDate() {
        return transactDate;
    }
}
