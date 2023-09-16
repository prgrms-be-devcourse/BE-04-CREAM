package com.programmers.dev.banking.application;


import com.programmers.dev.exception.BankingException;
import com.programmers.dev.settlement.domain.SettlementConfirmedEvent;
import com.programmers.dev.user.application.UserFindService;
import com.programmers.dev.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Service
@Transactional
@RequiredArgsConstructor
public class BankingWithSettlementEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(BankingWithSettlementEventHandler.class);

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

        try {
            doBankingService(user, event.money());
            logger.info("[은행 거래 완료] 사용자ID={}, 금액={}", user.getId(), event.money());
        } catch (BankingException exception) {
            logger.info("[은행 거래 실패] 사용자ID={}, 금액={}, 에러={}", event.userId(), event.money(), exception.getMessage());
        }
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
