package com.programmers.dev.kream.sellbids.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SELL_BIDS")
public class SellBids {

    /**
     * STAGED : 대기중
     * LIVE : 입찰 중
     * SHIPPED : 배송 완료
     * EXPIRED : 기한 만료
     * FINISHED : 정산 완료
     * CANCELLED : 취소 완료
     */
    private enum Status {
        STAGED, LIVE, SHIPPED, EXPIRED, FINISHED, CANCELLED
    }

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

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "ACCOUNT", nullable = false)
    private String account;

    @Column(name = "START_DATE", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;
}
