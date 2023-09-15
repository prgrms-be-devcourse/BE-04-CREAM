package com.programmers.dev.transaction.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTIONS")
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false, updatable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE")
    private TransactionType transactionType;

    @Column(name = "TRANSACTION_AMOUNT", nullable = false, updatable = false)
    private Long transactionAmount;

    @Column(name = "REGISTER_DATE", nullable = false, updatable = false)
    private LocalDateTime registerDate;

    protected Transaction() {
    }

    public Transaction(Long userId, TransactionType transactionType, Long transactionAmount) {
        this(userId, transactionType, transactionAmount, LocalDateTime.now());
    }

    private Transaction(Long userId, TransactionType transactionType, Long transactionAmount, LocalDateTime registerDate) {
        this.userId = userId;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.registerDate = registerDate;
    }
}
