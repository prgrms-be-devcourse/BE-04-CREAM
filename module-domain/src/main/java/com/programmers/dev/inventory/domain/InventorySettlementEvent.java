package com.programmers.dev.inventory.domain;

import com.programmers.dev.settlement.domain.Settlement;
import lombok.Getter;

@Getter
public abstract class InventorySettlementEvent {

    protected Long inventoryId;

    protected Long userId;

    protected InventorySettlementEvent( Long inventoryId, Long userId) {
        this.inventoryId = inventoryId;
        this.userId = userId;
    }

    public abstract Settlement.SettlementType getSettlementType();
}
