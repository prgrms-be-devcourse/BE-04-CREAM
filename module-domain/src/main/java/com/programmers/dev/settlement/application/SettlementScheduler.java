package com.programmers.dev.settlement.application;


import com.programmers.dev.event.EventManager;
import com.programmers.dev.settlement.domain.SettlementConfirmedEvent;
import com.programmers.dev.settlement.query.dto.SettlementView;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SettlementScheduler {

    private final SettlementService settlementService;

    /*
        상황 확인을 위해 현재 10초마다 스케쥴링 작동 설정
     */
    @Scheduled(initialDelay = 1000L, fixedDelay = 10000L)
    public void settlement() {
        List<SettlementView> settlementViews = settlementService.findSettlementTarget();

        for (SettlementView settlementView : settlementViews) {
            settlementService.settlementSpecificUser(settlementView.userId());
            EventManager.publish(new SettlementConfirmedEvent(settlementView.userId(), settlementView.money()));
        }
    }
}
