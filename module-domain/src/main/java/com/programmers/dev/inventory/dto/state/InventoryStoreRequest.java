package com.programmers.dev.inventory.dto.state;

import java.util.List;

public record InventoryStoreRequest(
        List<Long> inventoryIds
) {
}
