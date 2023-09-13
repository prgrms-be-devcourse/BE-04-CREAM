package com.programmers.dev.inventory.dto;


import java.util.List;

public record InventoryStoreResponse(
        List<Long> inventoryIds
) {
}
