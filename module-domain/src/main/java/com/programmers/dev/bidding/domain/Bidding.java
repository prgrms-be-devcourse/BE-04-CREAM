package com.programmers.dev.bidding.domain;

import com.programmers.dev.common.Status;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
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

    public enum DeliveryType{
        DELIVERY, IN_STOCK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BIDDING_ID")
    private Bidding bidding;

    @Column(name = "PRICE", nullable = false)
    private int price;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Enumerated(value = EnumType.STRING)
    private BiddingType biddingType;

    @Enumerated(value = EnumType.STRING)
    private DeliveryType deliveryType;

    @Column(name = "START_DATE", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    @Column(name = "TRANSACTION_DATE")
    private LocalDateTime transactionDate;

    protected Bidding() {

    }

    public static Bidding registerPurchaseBidding(Long userId, Long productId, Integer price, String storage, Long dueDate) throws CreamException{
        if ("delivery".equalsIgnoreCase(storage)) {
            return new Bidding(userId, productId, price, BiddingType.PURCHASE, DeliveryType.DELIVERY, dueDate);
        } else if ("in_stock".equalsIgnoreCase(storage)) {
            return new Bidding(userId, productId, price, BiddingType.PURCHASE, DeliveryType.IN_STOCK, dueDate);
        } else {
            throw new CreamException(ErrorCode.BAD_BUSINESS_LOGIC);
        }
    }

    public static Bidding registerSellBidding(Long userId, Long productId, Integer price, Long dueDate) {
        return new Bidding(userId, productId, price, BiddingType.SELL, null, dueDate);
    }

    private Bidding(Long userId, Long productId, int price, BiddingType biddingType, DeliveryType deliveryType, Long dueDate) {
        this.userId = userId;
        this.productId = productId;
        this.price = price;
        this.status = Status.LIVE;
        this.biddingType = biddingType;
        this.deliveryType = deliveryType;
        this.startDate = LocalDateTime.now();
        this.dueDate = this.startDate.plusDays(dueDate);
    }

    public static Bidding transactSellBidding(Long userId, String storage, Bidding sellBidding) {
        LocalDateTime transactionDate = LocalDateTime.now();
        sellBidding.transactSellBidding(transactionDate);
        if ("delivery".equalsIgnoreCase(storage)) {
            return new Bidding(userId, sellBidding.getProductId(), sellBidding, sellBidding.getPrice(), Status.IN_TRANSACTION, BiddingType.PURCHASE, DeliveryType.DELIVERY, LocalDateTime.now(), transactionDate);
        } else {
            return new Bidding(userId, sellBidding.getProductId(), sellBidding, sellBidding.getPrice(), Status.IN_TRANSACTION, BiddingType.PURCHASE, DeliveryType.IN_STOCK, LocalDateTime.now(), transactionDate);
        }
    }

    public static Bidding transactPurchaseBidding(Long userId, Bidding purchaseBidding) {
        LocalDateTime transactionDate = LocalDateTime.now();
        purchaseBidding.transactPurchaseBidding(purchaseBidding.getDeliveryType(), transactionDate);
        return new Bidding(userId, purchaseBidding.getProductId(), purchaseBidding, purchaseBidding.getPrice(), Status.IN_TRANSACTION, BiddingType.SELL, null, LocalDateTime.now(), transactionDate);
    }

    private Bidding(Long userId, Long productId,Bidding bidding, int price, Status status, BiddingType biddingType, DeliveryType deliveryType, LocalDateTime startDate, LocalDateTime transactionDate) {
        this.userId = userId;
        this.productId = productId;
        this.bidding = bidding;
        this.price = price;
        this.status = status;
        this.biddingType = biddingType;
        this.deliveryType = deliveryType;
        this.startDate = startDate;
        this.transactionDate = transactionDate;
    }

    public void transactSellBidding(LocalDateTime transactionDate) {
        this.status = Status.IN_TRANSACTION;
        this.transactionDate = transactionDate;
    }

    /*
    임시 구현 => 현재는 거래가 체결 될 경우 바로 배송되거나 바로 보관 된다.
     */
    public void transactPurchaseBidding(DeliveryType deliveryType, LocalDateTime transactionDate) {
        if (deliveryType.equals(DeliveryType.DELIVERY)) {
            this.status = Status.IN_TRANSACTION;
        } else {
            this.status = Status.IN_TRANSACTION;
        }
        this.transactionDate = transactionDate;
    }

    public void expire() {
        this.status = Status.EXPIRED;
    }

    public void checkAfterDueDate() {
        if (this.dueDate.isBefore(LocalDateTime.now())) {
            throw new CreamException(ErrorCode.AFTER_DUE_DATE);
        }
    }

    public void inspect(String result) throws CreamException{
        if ("ok".equalsIgnoreCase(result)) {
            this.status = Status.AUTHENTICATED;
        } else if ("fail".equalsIgnoreCase(result)) {
            this.status = Status.AUTHENTICATED_FAILED;
            this.bidding.status = Status.CANCELLED; // 관련 구매 상품은 거래 취소
        } else {
            throw new CreamException(ErrorCode.BAD_ARGUMENT);
        }
    }
}
