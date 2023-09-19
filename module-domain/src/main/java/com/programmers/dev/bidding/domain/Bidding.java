package com.programmers.dev.bidding.domain;

import com.programmers.dev.common.Status;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
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


    // == Constructor == //
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
        purchaseBidding.transactPurchaseBidding(transactionDate);
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

    // == Business Logic == //

    public void transactBidding(Bidding bidding) {
        this.bidding = bidding;
    }
    private void transactSellBidding(LocalDateTime transactionDate) {
        this.status = Status.IN_TRANSACTION;
        this.transactionDate = transactionDate;
    }

    private void transactPurchaseBidding(LocalDateTime transactionDate) {
        this.status = Status.IN_TRANSACTION;
        this.transactionDate = transactionDate;
    }

    public void expire() {
        this.status = Status.EXPIRED;
    }

    public void checkDurationOfBidding() {
        if (this.dueDate.isBefore(LocalDateTime.now())) {
            log.info("due date is over. biddingId : {}, dueDate : {}, now : {}", this.id, this.dueDate, LocalDateTime.now());
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
            log.info("cannot handle status. status : {}", result);
            throw new CreamException(ErrorCode.BAD_ARGUMENT);
        }
    }

    public void deposit() {
        if (this.deliveryType == DeliveryType.DELIVERY) {
            this.status = Status.DELIVERING;
        } else {
            this.status = Status.IN_WAREHOUSE;
        }
    }

    public void finish() {
        this.status = Status.FINISHED;
    }

    public void checkAuthorityOfUser(Long userId) throws CreamException{
        if (!this.userId.equals(userId)) {
            log.info("bidding's user id does not match. userId : {}, bidding.userId : {}", userId, this.userId);
            throw new CreamException(ErrorCode.NO_AUTHORITY);
        }
    }

    private void checkSellBiddingAuthenticated() {
        if (this.getBidding().getStatus() != Status.AUTHENTICATED) {
            log.info("Sell Bidding Product is not authenticated yet, bidding Id : {}, status : {}", this.getBidding().getId(), this.getBidding().getStatus());
            throw new CreamException(ErrorCode.INVALID_BIDDING_AUTHENTICATE);
        }
    }

    public void checkAbusing(Long userId) {
        if (this.userId.equals(userId)) {
            log.info("same seller and buyer. userId : {}", userId);
            throw new CreamException(ErrorCode.BAD_BUSINESS_LOGIC);
        }
    }

    public void checkBiddingBeforeDeposit(Long userId) {
        checkAuthorityOfUser(userId);
        checkSellBiddingAuthenticated();
    }

    public int getPoint() {
        return this.price / 100;
    }

    /*
    (판매자의 경우)
    입찰 중 => No Penalty.
    거래 증 => Penalty.
    입금 완료 => Cannot Cancel.
    (구매자의 경우)
    입찰 중 => No Penalty.
    거래 중 => Penalty.
    검수 완료 => Cannot Cancel.
     */
    public int cancel() {
        if (this.biddingType == BiddingType.SELL) {
            Bidding purchaseBidding = this.getBidding();
            if (this.status == Status.LIVE) {
                this.status = Status.CANCELLED;
                return 0;
            } else if (this.status == Status.FINISHED) {
                log.info("bidding is finished. sellBidding : {}, purchaseBidding : {}", this.status, purchaseBidding.status);
                throw new CreamException(ErrorCode.CANNOT_CANCEL);
            } else if (this.status == Status.AUTHENTICATED_FAILED) {
                log.info("bidding is not authenticated. bidding id : {}", this.id);
                throw new CreamException(ErrorCode.CANNOT_CANCEL);
            } else if (this.status == Status.IN_TRANSACTION) {
                if (purchaseBidding.status == Status.IN_TRANSACTION) {
                    this.status = Status.CANCELLED;
                    purchaseBidding.status = Status.CANCELLED;
                    return (this.price / 100);
                } else {
                    log.info("purchase bidding is already DELIVERED OR IN STOCK. sellBidding : {}, purchaseBidding : {}", this.status, purchaseBidding.status);
                    throw new CreamException(ErrorCode.CANNOT_CANCEL);
                }
            }
        } else {
            Bidding sellBidding = this.getBidding();
            if (this.status == Status.LIVE) {
                this.status = Status.CANCELLED;
                return 0;
            } else if (this.status == Status.FINISHED) {
                log.info("bidding is finished. sellBidding : {}, purchaseBidding : {}", sellBidding.status, this.status);
                throw new CreamException(ErrorCode.CANNOT_CANCEL);
            } else if (this.status == Status.IN_TRANSACTION) {
                if (sellBidding.status == Status.IN_TRANSACTION) {
                    this.status = Status.CANCELLED;
                    sellBidding.status = Status.CANCELLED;
                    return (this.price / 100);
                } else {
                    log.info("sell bidding is already BEING DELIVERED. sellBidding : {}, purchaseBidding : {}", sellBidding.status, this.status);
                    throw new CreamException(ErrorCode.CANNOT_CANCEL);
                }
            }
        }
        throw new CreamException(ErrorCode.SERVER_ERROR);
    }

}
