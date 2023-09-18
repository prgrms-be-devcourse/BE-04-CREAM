package com.programmers.dev.banking.application;


import com.programmers.dev.banking.aop.Deposit;
import com.programmers.dev.banking.aop.Withdraw;
import com.programmers.dev.user.domain.User;
import org.springframework.stereotype.Service;


@Service
public class BankingService {

    @Withdraw
    public void withdraw(User user, Long money) {
        user.withdraw(money);
    }

    @Deposit
    public void deposit(User user, Long money) {
        user.deposit(money);
    }
}
