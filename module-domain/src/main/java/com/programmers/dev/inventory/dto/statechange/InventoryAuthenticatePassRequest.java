package com.programmers.dev.inventory.dto.statechange;

import com.programmers.dev.inventory.domain.ProductQuality;


public record InventoryAuthenticatePassRequest(
        ProductQuality productQuality
) {
}
