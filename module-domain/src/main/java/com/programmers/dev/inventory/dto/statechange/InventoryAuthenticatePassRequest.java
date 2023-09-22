package com.programmers.dev.inventory.dto.statechange;


import com.programmers.dev.inventory.domain.Inventory;

public record InventoryAuthenticatePassRequest(
        Inventory.ProductQuality productQuality
) {
}
