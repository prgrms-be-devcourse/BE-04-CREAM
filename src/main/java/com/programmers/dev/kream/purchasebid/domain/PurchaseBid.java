package com.programmers.dev.kream.purchasebid.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PURCHASE_BIDS")
public class PurchaseBid {

    /**
     * 현재 스프린트 시나리오에 맞는 상태만 구현
     * BIDDING : 입찰 중
     * EXPIRED : 기한 만료
     * IN_WAREHOUSE : 입고 완료
     * AUTHENTICATED : 검수 합격
     * DELIVERING : 배송 중
     * FINISHED : 거래 완료
     */
    public enum PurchaseBidStatus { BIDDING, EXPIRED, IN_WAREHOUSE, AUTHENTICATED, DELIVERING, FINISHED }

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
    private PurchaseBidStatus purchaseBidStatus;

    @Column(name = "START_DATE", updatable = false)
    private LocalDateTime startDate;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    protected PurchaseBid() {}
}
