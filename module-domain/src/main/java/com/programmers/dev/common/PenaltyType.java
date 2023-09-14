package com.programmers.dev.common;


import lombok.Getter;

@Getter
public enum PenaltyType {

    PRODCUT_MISMATCHED(0.1, "상품 불일치"),

    PRODUCT_SIZED_MISMATCHED(0.1, "사이즈 불일치"),

    PRODUCT_FAKED(0.15, "가품"),

    PRODUCT_DAMEGED(0.15, "손상")
    ;

    private final Double penaltyRate;

    private final String description;

    PenaltyType(Double penaltyRate, String description) {
        this.penaltyRate = penaltyRate;
        this.description = description;
    }
}
