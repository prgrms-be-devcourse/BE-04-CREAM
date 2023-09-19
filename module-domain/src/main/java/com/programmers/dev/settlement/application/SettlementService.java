package com.programmers.dev.settlement.application;


import com.programmers.dev.settlement.domain.Settlement;
import com.programmers.dev.settlement.domain.SettlementRepository;
import com.programmers.dev.settlement.query.dto.SettlementView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;

    @Transactional(readOnly = true)
    public List<Settlement> findByUserId(Long userId) {
        return settlementRepository.findAllByUserId(userId);
    }

    public void save(Long userId, Settlement.SettlementType settlementType, Long money) {
        settlementRepository.save(new Settlement(userId, settlementType, money));
    }

    public void settlementSpecificUser(Long userId) {
        settlementRepository.deleteAllByUserId(userId);
    }

    public List<SettlementView> findSettlementTarget() {
        return settlementRepository.findSettlementView();
    }
}
