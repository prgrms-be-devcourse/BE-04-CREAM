package com.programmers.dev.inventory.domain;


import com.programmers.dev.common.Status;
import com.programmers.dev.common.TransactionType;
import com.programmers.dev.user.domain.Address;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "INVENTORIES")
@Getter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "PRODUCT_ID", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "PRICE")
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "PRODUCT_QUALITY")
    private ProductQuality productQuality;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_STATUS", nullable = false)
    private Status status;

    @Embedded
    private Address address;

    @Column(name = "START_DATE", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "TRANSACTION_DATE", updatable = false)
    private LocalDateTime transactionDate;

    public Inventory(Long userId, Long productId, TransactionType transactionType, Status status, Address address, LocalDateTime startDate) {
        this(userId, productId, null, null, transactionType, status, address, startDate, null);
    }

    public Inventory(Long userId, Long productId, Long price, ProductQuality productQuality, TransactionType transactionType, Status status, Address address, LocalDateTime startDate, LocalDateTime transactionDate) {
        this.userId = userId;
        this.productId = productId;
        this.price = price;
        this.productQuality = productQuality;
        this.transactionType = transactionType;
        this.status = status;
        this.address = address;
        this.startDate = startDate;
        this.transactionDate = transactionDate;
    }

    protected Inventory() {
    }

    public void changeTransactionStatus(Status status) {
        this.status = status;
    }
}
