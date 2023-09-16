package com.programmers.dev.inventory.application;


import com.programmers.dev.common.CostCalculator;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventory.dto.InventoryRegisterRequest;
import com.programmers.dev.inventory.dto.InventoryRegisterResponse;
import com.programmers.dev.banking.application.BankingService;
import com.programmers.dev.user.application.UserFindService;
import com.programmers.dev.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryRegisterService {

    private final InventoryRepository inventoryRepository;

    private final UserFindService userFindService;

    private final CostCalculator costCalculator;

    private final BankingService bankingService;

    public InventoryRegisterResponse register(Long userId, InventoryRegisterRequest request) {
        User user = userFindService.findById(userId);

        Long protectionCost = costCalculator.calcualteProtectionCost(request.quantity());
        bankingService.withdraw(user, protectionCost);

        List<Long> inventoryIds = request.toEntities(userId)
                .stream()
                .map(inventoryRepository::save)
                .map(Inventory::getId)
                .toList();

        return new InventoryRegisterResponse(inventoryIds);
    }
}
