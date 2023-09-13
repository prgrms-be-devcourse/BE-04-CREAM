package com.programmers.dev.payment;


import com.programmers.dev.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {

    private final static Long PROTECTION_MONEY = 3_000L;

    public void accountTransaction(User purchaser, User seller, Long price) {
        purchaser.withdraw(price);
        seller.deposit(price);
    }

    public void payProtectionMoney(User seller, Long qauntity) {
        seller.withdraw(calcualteProtectionMoney(qauntity));
    }

    private Long calcualteProtectionMoney(Long quantity) {
        return quantity * PROTECTION_MONEY;
    }
}
