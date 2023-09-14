package com.programmers.dev.payment.application;


import com.programmers.dev.user.domain.User;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {

    public void pay(User user, Long money) {
        user.withdraw(money);
    }
}
