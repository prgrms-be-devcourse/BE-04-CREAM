package com.programmers.dev.kream.sellbidding.domain;

import com.programmers.dev.kream.common.bidding.Status;
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
}
