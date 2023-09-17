package com.programmers.dev.inventoryorder.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "INVENTORY_ORDERS")
@Getter
public class InventoryOrder {

    enum InventoryOrderStatus {
        DELIVERING,

        SHIPPED,

        FINISHED,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "INVENTORY_ID", nullable = false, updatable = false)
    private Long inventoryId;

    @Column(name = "ORDERD_PRICE", nullable = false, updatable = false)
    private Long orderdPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private InventoryOrderStatus inventoryOrderStatus;

    @Column(name = "TRANSACTION_DATE", updatable = false)
    private LocalDateTime transactionDate;

    protected InventoryOrder() {
    }

    public InventoryOrder(Long userId, Long inventoryId, Long orderdPrice, LocalDateTime transactionDate) {
        this.userId = userId;
        this.inventoryId = inventoryId;
        this.orderdPrice = orderdPrice;
        this.inventoryOrderStatus = InventoryOrderStatus.DELIVERING;
        this.transactionDate = transactionDate;
    }
}
