package com.programmers.dev.kream.common.application;


import com.programmers.dev.kream.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class BankService {

    //Todo: User 가 Account 객체 가지도록 고도화 예정
    public void accountTransaction(User purchaser, User seller, Integer price) {
        purchaser.withdraw(Long.valueOf(price));
        seller.deposit(price);
    }
}
