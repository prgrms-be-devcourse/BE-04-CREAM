package com.programmers.dev.inventory.application;

import com.programmers.dev.banking.application.BankingService;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.dto.InventoryOrderRequest;
import com.programmers.dev.inventory.dto.InventoryOrderResponse;
import com.programmers.dev.inventoryorder.domain.InventoryOrder;
import com.programmers.dev.inventoryorder.domain.InventoryOrderRepository;
import com.programmers.dev.user.application.UserFindService;
import com.programmers.dev.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryOrderService {

    private final InventoryOrderRepository inventoryOrderRepository;

    private final InventoryFindService inventoryFindService;

    private final UserFindService userFindService;

    private final BankingService bankingService;

    public InventoryOrderResponse order(Long userId, InventoryOrderRequest request, Inventory.ProductQuality productQuality) {
        Inventory inventory = inventoryFindService.findOrderableInventory(request.productId(), request.price(), productQuality);

        User user = userFindService.findById(userId);
        bankingService.withdraw(user, request.price());

        LocalDateTime transactionDate = getCurrrentTime();
        inventory.ordered(transactionDate);
        InventoryOrder inventoryOrder = inventoryOrderRepository.save(
                new InventoryOrder(userId, inventory.getId(), request.price(), transactionDate)
        );

        return new InventoryOrderResponse(inventoryOrder.getId());
    }

    private LocalDateTime getCurrrentTime() {
        return LocalDateTime.now();
    }
}
