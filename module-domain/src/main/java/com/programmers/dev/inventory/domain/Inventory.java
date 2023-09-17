package com.programmers.dev.inventory.domain;


import com.programmers.dev.common.Status;
import com.programmers.dev.event.EventManager;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.user.domain.Address;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.programmers.dev.common.CostType.PROTECTION;
import static com.programmers.dev.common.CostType.RETURN_SHIPPING;

@Entity
@Table(name = "INVENTORIES")
@Getter
public class Inventory {

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
    @Column(name = "TRANSACTION_STATUS", nullable = false)
    private Status status;

    @Embedded
    private Address address;

    @Column(name = "START_DATE", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "TRANSACTION_DATE", updatable = false)
    private LocalDateTime transactionDate;

    protected Inventory() {
    }

    public Inventory(Long userId, Long productId, Status status, Address address, LocalDateTime startDate) {
        this(userId, productId, null, null, status, address, startDate, null);
    }

    public Inventory(Long userId, Long productId, Long price, ProductQuality productQuality, Status status, Address address, LocalDateTime startDate, LocalDateTime transactionDate) {
        this.userId = userId;
        this.productId = productId;
        this.price = price;
        this.productQuality = productQuality;
        this.status = status;
        this.address = address;
        this.startDate = startDate;
        this.transactionDate = transactionDate;
    }

    public void changeStatusInWarehouse() {
        validate(Status.OUT_WAREHOUSE);
        changeStatus(Status.IN_WAREHOUSE);
    }

    public void changeStatusAuthenticatedWithProductQuality(ProductQuality productQuality) {
        validate(Status.IN_WAREHOUSE);
        changeStatus(Status.AUTHENTICATED);
        changeProductQuality(productQuality);

        EventManager.publish(new InventoryAuthenticationPassedEvent(this.userId, PROTECTION.getCost()));
    }

    public void changeStatusAuthenticationFailed(Long penaltyCost) {
        validate(Status.IN_WAREHOUSE);
        changeStatus(Status.AUTHENTICATION_FAILED);

        EventManager.publish(new InventoryAuthenticationFailedEvent(this.userId, penaltyCost, RETURN_SHIPPING.getCost()));
    }

    public void changeStatusLive() {
        validate(Status.AUTHENTICATED);
        changeStatus(Status.LIVE);
    }

    public void payed(LocalDateTime transactionDate) {
        validate(Status.LIVE);
        changeStatus(Status.PAYED);
        setTransactionDate(transactionDate);
        //배송 이벤트 발행
    }

    public void setPrice(Long price) {
        validate(price);
        this.price = price;
    }

    private void changeStatus(Status status) {
        this.status = status;
    }

    private void validate(Long price) {
        if (price <= 0) {
            throw new CreamException(ErrorCode.BAD_BUSINESS_LOGIC);
        }
    }

    private void validate(Status status) {
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

    private void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
