package com.programmers.dev.inventory.domain;

import com.programmers.dev.settlement.domain.Settlement;
import lombok.Getter;

@Getter
public class InventoryOrderedEvent extends InventorySettlementEvent{
    private final Long orderedPrice;

    public InventoryOrderedEvent(Long userId, Long orderedPrice) {
        super(userId);
        this.orderedPrice = orderedPrice;
    }

    @Override
    public Settlement.SettlementType getSettlementType() {
        return Settlement.SettlementType.DEPOSIT;
    }
}
