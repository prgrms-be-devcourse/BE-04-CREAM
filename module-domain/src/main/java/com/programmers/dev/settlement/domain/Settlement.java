package com.programmers.dev.settlement.domain;


import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "SETTLEMENTS")
@Getter
@SQLDelete(sql = "UPDATE SETTLEMENTS SET SETTLEMENTED_DATE = NOW() where ID = ?")
@Where(clause = "SETTLEMENTED_DATE is NULL")
public class Settlement {

    public enum SettlementType {
        WITHDRAW,
        DEPOSIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false, updatable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "SETTLEMENT_TYPE")
    private SettlementType settlementType;

    @Column(name = "SETTLEMENT_AMOUNT", nullable = false, updatable = false)
    private Long settlementAmount;

    @Column(name = "REGISTER_DATE", nullable = false, updatable = false)
    private LocalDateTime registerDate;

    @Column(name = "SETTLEMENTED_DATE")
    private LocalDateTime deleteDate;

    protected Settlement() {
    }

    public Settlement(Long userId, SettlementType settlementType, Long settlementAmount) {
        this(userId, settlementType, settlementAmount, LocalDateTime.now());
    }

    private Settlement(Long userId, SettlementType settlementType, Long settlementAmount, LocalDateTime registerDate) {
        this.userId = userId;
        this.settlementType = settlementType;
        this.settlementAmount = settlementAmount;
        this.registerDate = registerDate;
    }
}
