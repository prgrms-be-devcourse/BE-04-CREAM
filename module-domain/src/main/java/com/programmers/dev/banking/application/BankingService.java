package com.programmers.dev.banking.application;


import com.programmers.dev.user.domain.User;
import org.springframework.stereotype.Service;


@Service
public class BankingService {

    public void withdraw(User user, Long money) {
        user.withdraw(money);
    }

    public void deposit(User user, Long money) {
        user.deposit(money);
    }
}
