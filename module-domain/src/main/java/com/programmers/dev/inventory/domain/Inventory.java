package com.programmers.dev.inventory.domain;


import com.programmers.dev.common.Status;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.user.domain.Address;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "INVENTORIES")
@Getter
public class Inventory {

    public enum InventoryType {
        PURCHASE,
        SELL
    }

    public enum ProductQuality {
        COMPLETE,
        INCOMPLETE,
    }

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
    private InventoryType inventoryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_STATUS", nullable = false)
    private Status status;

    @Embedded
    private Address address;

    @Column(name = "START_DATE", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "TRANSACTION_DATE", updatable = false)
    private LocalDateTime transactionDate;

    public Inventory(Long userId, Long productId, InventoryType inventoryType, Status status, Address address, LocalDateTime startDate) {
        this(userId, productId, null, null, inventoryType, status, address, startDate, null);
    }

    private Inventory(Long userId, Long productId, Long price, ProductQuality productQuality, InventoryType inventoryType, Status status, Address address, LocalDateTime startDate, LocalDateTime transactionDate) {
        this.userId = userId;
        this.productId = productId;
        this.price = price;
        this.productQuality = productQuality;
        this.inventoryType = inventoryType;
        this.status = status;
        this.address = address;
        this.startDate = startDate;
        this.transactionDate = transactionDate;
    }

    protected Inventory() {
    }

    public void changeStatusInWarehouse() {
        statusValidate(Status.OUT_WAREHOUSE);
        changeStatus(Status.IN_WAREHOUSE);
    }

    public void changeStatusAuthenticatedWithProductQuality(ProductQuality productQuality) {
        statusValidate(Status.IN_WAREHOUSE);
        changeStatus(Status.AUTHENTICATED);
        changeProductQuality(productQuality);
    }

    public void changeStatusReturnShipping() {
        statusValidate(Status.IN_WAREHOUSE);
        changeStatus(Status.RETURN_SHIPPING);
    }

    private void changeStatus(Status status) {
        this.status = status;
    }

    private void statusValidate(Status status) {
        if (this.status != status) {
            throw new CreamException(ErrorCode.BAD_BUSINESS_LOGIC);
        }
    }

    private void changeProductQuality(ProductQuality productQuality) {
        if (this.productQuality != null) {
            throw new CreamException(ErrorCode.BAD_BUSINESS_LOGIC);
        }

        this.productQuality = productQuality;
    }
}
