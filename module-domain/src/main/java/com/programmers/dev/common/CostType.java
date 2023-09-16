package com.programmers.dev.common;


import lombok.Getter;

@Getter
public enum CostType {

    PROTECTION(3000L, "보증금"),

    RETURN_SHIPPING(5000L, "반송비")
    ;

    private final Long cost;

    private final String description;

    CostType(Long cost, String description) {
        this.cost = cost;
        this.description = description;
    }
}
