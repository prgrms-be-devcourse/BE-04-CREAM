package com.programmers.dev.inventory.dto;


import java.util.List;

public record InventoryRegisterResponse(
        List<Long> inventoryIds
) {
}
