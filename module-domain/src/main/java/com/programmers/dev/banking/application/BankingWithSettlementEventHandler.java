package com.programmers.dev.banking.application;


import com.programmers.dev.settlement.domain.SettlementConfirmedEvent;
import com.programmers.dev.user.application.UserFindService;
import com.programmers.dev.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BankingWithSettlementEventHandler {

    private final UserFindService userFindService;

    private final BankingService bankingService;

    @Async
    @EventListener(SettlementConfirmedEvent.class)
    @TransactionalEventListener(
            classes = SettlementConfirmedEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(SettlementConfirmedEvent event) {
        User user = userFindService.findById(event.userId());
        doBankingService(user, event.money());
    }

    private void doBankingService(User user, Long money) {
        long transferMoney = Math.abs(money);

        if (money < 0) {
            bankingService.withdraw(user, transferMoney);
            return;
        }

        bankingService.deposit(user, transferMoney);
    }
}
