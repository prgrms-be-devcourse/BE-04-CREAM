package com.programmers.dev.inventory.application;


import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventory.dto.InventoryStoreRequest;
import com.programmers.dev.inventory.dto.InventoryStoreResponse;
import com.programmers.dev.payment.PaymentService;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryStoreService {

    private final InventoryRepository inventoryRepository;

    private final UserRepository userRepository;

    private final PaymentService paymentService;

    @Transactional
    public InventoryStoreResponse store(Long userId, InventoryStoreRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));

        paymentService.payProtectionMoney(user, request.quantity());

        List<Long> inventoryIds = request.toEntities(userId)
                .stream()
                .map(inventoryRepository::save)
                .map(Inventory::getId)
                .toList();

        return new InventoryStoreResponse(inventoryIds);
    }
}
