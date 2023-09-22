package com.programmers.dev.inventory.dto.statechange;

import java.util.List;

public record InventoryArrivedRequest(
        List<Long> inventoryIds
) {
}
