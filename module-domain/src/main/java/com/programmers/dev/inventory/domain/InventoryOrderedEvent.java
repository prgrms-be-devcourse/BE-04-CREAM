package com.programmers.dev.inventory.domain;

import com.programmers.dev.settlement.domain.Settlement;
import lombok.Getter;

@Getter
public class InventoryOrderedEvent extends InventorySettlementEvent{
    private final Long orderedMoney;

    public InventoryOrderedEvent(Long inventoryId, Long userId, Long orderedMoney) {
        super(inventoryId, userId);
        this.orderedMoney = orderedMoney;
    }

    @Override
    public Settlement.SettlementType getSettlementType() {
        return Settlement.SettlementType.DEPOSIT;
    }
}
