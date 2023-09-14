package com.programmers.dev.inventory.statechange.dto;

import java.util.List;

public record InventoryStoreRequest(
        List<Long> inventoryIds
) {
}
