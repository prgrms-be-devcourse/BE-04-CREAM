package com.programmers.dev.inventory.application;


import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.dto.statechange.InventoryAuthenticateFailRequest;
import com.programmers.dev.inventory.dto.statechange.InventoryAuthenticatePassRequest;
import com.programmers.dev.inventory.dto.statechange.InventoryArrivedRequest;
import com.programmers.dev.payment.application.PaymentCalculator;
import com.programmers.dev.product.application.ProductService;
import com.programmers.dev.product.dto.ProductResponse;
import com.programmers.dev.transaction.application.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.programmers.dev.common.CostType.*;
import static com.programmers.dev.transaction.domain.Transaction.TransactionType.DEPOSIT;
import static com.programmers.dev.transaction.domain.Transaction.TransactionType.WITHDRAW;


@Service
@Transactional
@RequiredArgsConstructor
public class InventoryStateChangeService {

    private final InventoryFindService inventoryFindService;

    private final PaymentCalculator paymentCalculator;

    private final ProductService productService;

    private final TransactionService transactionService;

    public void warehouseArrived(InventoryArrivedRequest request) {
        for (Long inventoryId : request.inventoryIds()) {
            Inventory inventory = inventoryFindService.findById(inventoryId);
            inventory.changeStatusInWarehouse();
        }
    }

    public void authenticatePass(Long inventoryId, InventoryAuthenticatePassRequest request) {
        Inventory inventory = inventoryFindService.findById(inventoryId);
        inventory.changeStatusAuthenticatedWithProductQuality(request.productQuality());

        transactionService.save(inventory.getUserId(), DEPOSIT, PROTECTION.getCost());
    }

    public void authenticateFail(Long inventoryId, InventoryAuthenticateFailRequest request) {
        Inventory inventory = inventoryFindService.findById(inventoryId);
        inventory.changeStatusReturnShipping();

        ProductResponse productResponse = productService.findById(inventory.getProductId());

        Long penaltyCost = paymentCalculator.calculatePenaltyCost(productResponse.productInfo().getReleasePrice(), request.penaltyType());
        Long paymentAmount = penaltyCost + RETURN_SHIPPING.getCost();

        transactionService.save(inventory.getUserId(), WITHDRAW, paymentAmount);
    }
}



