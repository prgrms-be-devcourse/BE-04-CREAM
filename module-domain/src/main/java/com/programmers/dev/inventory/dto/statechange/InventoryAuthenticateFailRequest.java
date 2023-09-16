package com.programmers.dev.inventory.dto.statechange;

import com.programmers.dev.common.PenaltyType;


public record InventoryAuthenticateFailRequest(
        PenaltyType penaltyType
) {
}
