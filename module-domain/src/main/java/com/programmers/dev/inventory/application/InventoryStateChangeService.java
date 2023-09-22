package com.programmers.dev.inventory.application;


import com.programmers.dev.common.CostCalculator;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.dto.statechange.*;
import com.programmers.dev.inventoryorder.application.InvenotryOrderFindService;
import com.programmers.dev.inventoryorder.domain.InventoryOrder;
import com.programmers.dev.product.application.ProductService;
import com.programmers.dev.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional
@RequiredArgsConstructor
public class InventoryStateChangeService {

    private final InventoryFindService inventoryFindService;

    private final InvenotryOrderFindService invenotryOrderFindService;

    private final CostCalculator costCalculator;

    private final ProductService productService;

    public void warehouseArrived(InventoryArrivedRequest request) {
        for (Long inventoryId : request.inventoryIds()) {
            Inventory inventory = inventoryFindService.findById(inventoryId);
            inventory.stockInWarehouse();
        }
    }

    public void authenticatePassed(Long inventoryId, InventoryAuthenticatePassRequest request) {
        Inventory inventory = inventoryFindService.findById(inventoryId);
        inventory.authenticationPassedWithProductQuality(request.productQuality());
    }

    public void authenticateFailed(Long inventoryId, InventoryAuthenticateFailRequest request) {
        Inventory inventory = inventoryFindService.findById(inventoryId);
        ProductResponse productResponse = productService.findById(inventory.getProductId());

        Long penaltyCost = costCalculator.calculatePenaltyCost(productResponse.productInfo().getReleasePrice(), request.penaltyType());
        inventory.authenticationFailed(penaltyCost);
    }

    public void setPrice(Long inventoryId, InventorySetPriceRequest request) {
        Inventory inventory = inventoryFindService.findById(inventoryId);
        inventory.lived(request.hopedPrice());
    }

    public void finished(Long inventoryId) {
        Inventory inventory = inventoryFindService.findById(inventoryId);
        InventoryOrder inventoryOrder = invenotryOrderFindService.findByInventoryId(inventoryId);

        inventory.finished();
        inventoryOrder.shipped();
    }
}
