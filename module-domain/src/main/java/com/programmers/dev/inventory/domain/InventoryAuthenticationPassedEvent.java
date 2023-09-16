package com.programmers.dev.inventory.domain;


import com.programmers.dev.settlement.domain.Settlement;
import lombok.Getter;

@Getter
public class InventoryAuthenticationPassedEvent extends InventorySettlementEvent{

    private final Long protectionMoney;

    public InventoryAuthenticationPassedEvent(Long userId, Long protectionMoney) {
        super(userId);
        this.protectionMoney = protectionMoney;
    }

    @Override
    public Settlement.SettlementType getSettlementType() {
        return Settlement.SettlementType.DEPOSIT;
    }
}
