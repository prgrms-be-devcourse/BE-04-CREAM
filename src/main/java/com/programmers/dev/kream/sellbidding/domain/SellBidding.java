package com.programmers.dev.kream.sellbidding.domain;

import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;
import com.programmers.dev.kream.sellbidding.ui.SellBiddingRequest;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SELL_BIDDINGS")
public class SellBidding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SELL_BIDDER_ID", nullable = false)
    private Long sellBidderId;

    @Column(name = "SIZED_PRODUCT_ID", nullable = false)
    private Long sizedProductId;

    @Column(name = "PRICE", nullable = false)
    private Integer price;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    @Column(name = "START_DATE", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDateTime dueDate;

    protected SellBidding() { }

    /**
     * 판매 입찰 등록시 사용하는 Constructor
     * @param sellBidderId : 판매자 id
     * @param sizedProductId : 상품 id
     * @param price : 판매 입찰 가격
     * @param dueDate : 입찰 마감 기한
     */
    private SellBidding(Long sellBidderId, Long sizedProductId, Integer price, Long dueDate) {
        this.sellBidderId = sellBidderId;
        this.sizedProductId = sizedProductId;
        this.price = price;
        this.status = Status.LIVE;
        this.startDate = LocalDateTime.now();
        this.dueDate = startDate.plusDays(dueDate);
    }

    /**
     * 구매 입찰에 등록된 건을 거래하려고 하는 경우 사용하는 Constructor
     * @param sellBidderId  : 판매자 id
     * @param sizedProductId : 상품 id
     * @param price : 판매 입찰 가격
     * @param dueDate : 입찰 마감 기한
     */
    private SellBidding(Long sellBidderId, Long sizedProductId, Integer price, LocalDateTime dueDate) {
        this.sellBidderId = sellBidderId;
        this.sizedProductId = sizedProductId;
        this.price = price;
        this.status = Status.SHIPPED;
        this.startDate = LocalDateTime.now();
        this.dueDate = dueDate;
    }

    /**
     * 판매 입찰 등록시 사용하는 Constructor
     * @param sellBidderId : 판매자 id
     * @param sizedProductId : 상품 id
     * @param sellBiddingRequest : 판매 입찰 정보
     * @return  SellBidding
     */
    public static SellBidding of(Long sellBidderId, Long sizedProductId, SellBiddingRequest sellBiddingRequest) {
        return new SellBidding(
                sellBidderId,
                sizedProductId,
                sellBiddingRequest.price(),
                sellBiddingRequest.dueDate());
    }

    /**
     * todo : 구매입찰과 가격 타입 일치화 시키기
     * todo : 입찰이 성사 됐을 때 성사된 날짜를 가지는 컬럼을 가지는 것은 어떠 할 지 논의
     * 구매 입찰에 등록된 건을 거래하려고 하는 경우 사용하는 Constructor
     * @param sellBidderId : 판매자 id
     * @param purchaseBidding : 구매 입찰
     * @return  SellBidding
     */
    public static SellBidding of(Long sellBidderId, PurchaseBidding purchaseBidding) {
        return new SellBidding(
                sellBidderId,
                purchaseBidding.getSizedProductId(),
                Integer.valueOf(purchaseBidding.getPrice().toString()),
                purchaseBidding.getDueDate()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getSellBidderId() {
        return sellBidderId;
    }

    public Long getSizedProductId() {
        return sizedProductId;
    }

    public Integer getPrice() {
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
