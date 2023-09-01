package com.programmers.dev.kream.purchasebidding.ui.dto;


public record SelectLine(

        boolean lived,

        String size,

        String sizedProductId,

        String price
) {
    @Override
    public String toString() {
        return "SelectLine{" +
                "lived=" + lived +
                ", size='" + size + '\'' +
                ", sizedProductId='" + sizedProductId + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
