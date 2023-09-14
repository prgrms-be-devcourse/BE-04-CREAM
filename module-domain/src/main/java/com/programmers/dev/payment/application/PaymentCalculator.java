package com.programmers.dev.payment.application;


import com.programmers.dev.common.PenaltyType;
import com.programmers.dev.common.CostType;
import org.springframework.stereotype.Service;

@Service
public class PaymentCalculator {

    public Long calcualteProtectionCost(Long quantity) {
        return quantity * CostType.PROTECTION.getCost();
    }

    public Long calculatePenaltyCost(Long productPrice, PenaltyType penaltyType) {
        return (long) switch (penaltyType) {
            case PRODCUT_MISMATCHED -> PenaltyType.PRODCUT_MISMATCHED.getPenaltyRate() * productPrice;
            case PRODUCT_SIZED_MISMATCHED -> PenaltyType.PRODUCT_SIZED_MISMATCHED.getPenaltyRate() * productPrice;
            case PRODUCT_FAKED -> PenaltyType.PRODUCT_FAKED.getPenaltyRate() * productPrice;
            case PRODUCT_DAMEGED -> PenaltyType.PRODUCT_DAMEGED.getPenaltyRate() * productPrice;
        };
    }
}
