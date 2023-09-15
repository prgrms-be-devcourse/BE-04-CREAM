package com.programmers.dev.transaction.application;


import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.transaction.domain.Transaction;
import com.programmers.dev.transaction.domain.TransactionRepository;
import com.programmers.dev.transaction.domain.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public void save(Long userId, TransactionType transactionType, Long money) {
        Transaction transaction = new Transaction(userId, transactionType, money);
        transactionRepository.save(transaction);
    }

    public List<Transaction> findByUserId(Long userId) {
        return transactionRepository.findAllByUserId(userId);
    }
}
