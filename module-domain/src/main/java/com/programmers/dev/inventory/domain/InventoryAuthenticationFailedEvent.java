package com.programmers.dev.inventory.domain;


import com.programmers.dev.settlement.domain.Settlement;
import lombok.Getter;

@Getter
public class InventoryAuthenticationFailedEvent extends InventorySettlementEvent{

    private final Long penaltyMoney;

    private final Long returnShippingMoney;

    public InventoryAuthenticationFailedEvent(Long userId, Long penaltyMoney, Long returnShippingMoney) {
        super(userId);
        this.penaltyMoney = penaltyMoney;
        this.returnShippingMoney = returnShippingMoney;
    }

    @Override
    public Settlement.SettlementType getSettlementType() {
        return Settlement.SettlementType.WITHDRAW;
    }
}
