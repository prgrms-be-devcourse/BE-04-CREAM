package com.programmers.dev.inventory.application;


import com.programmers.dev.inventory.domain.InventoryAuthenticationFailedEvent;
import com.programmers.dev.inventory.domain.InventoryAuthenticationPassedEvent;
import com.programmers.dev.inventory.domain.InventoryOrderedEvent;
import com.programmers.dev.settlement.application.SettlementService;
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
public class InventorySettlementEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(InventorySettlementEventHandler.class);

    private final SettlementService settlementService;

    @Async
    @EventListener(InventoryAuthenticationPassedEvent.class)
    @TransactionalEventListener(
            classes = InventoryAuthenticationPassedEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(InventoryAuthenticationPassedEvent event) {
        settlementService.save(event.getUserId(), event.getSettlementType(), event.getProtectionMoney());

        logger.info("[정산등록 : 보관판매 검수 합격]  (사용자ID={}, 환급 보증금={}", event.getUserId(), event.getProtectionMoney());
    }

    @Async
    @EventListener(InventoryAuthenticationFailedEvent.class)
    @TransactionalEventListener(
            classes = InventoryAuthenticationFailedEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(InventoryAuthenticationFailedEvent event) {
        settlementService.save(event.getUserId(), event.getSettlementType(), -event.getPenaltyMoney());
        settlementService.save(event.getUserId(), event.getSettlementType(), -event.getReturnShippingMoney());

        logger.info("[정산등록 : 보관판매 검수 실패] 사용자ID={}, 패널티 비용={}, 반송비={}", event.getUserId(), event.getPenaltyMoney(), event.getReturnShippingMoney());
    }

    @Async
    @EventListener(InventoryOrderedEvent.class)
    @TransactionalEventListener(
            classes = InventoryOrderedEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(InventoryOrderedEvent event) {
        settlementService.save(event.getUserId(), event.getSettlementType(), event.getOrderedPrice());

        logger.info("[정산등록 : 보관판매 등록 상품 구매됨] 사용자ID={}, 환급 금액={}", event.getUserId(), event.getOrderedPrice());
    }
}
