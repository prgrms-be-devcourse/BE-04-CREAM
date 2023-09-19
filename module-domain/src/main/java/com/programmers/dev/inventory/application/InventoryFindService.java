package com.programmers.dev.inventory.application;


import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryFindService {

    private final InventoryRepository inventoryRepository;

    public Inventory findById(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));
    }

    public Inventory findOrderableInventory(Long productId, Long price, Inventory.ProductQuality productQuality) {
        return inventoryRepository.findOrderableInventory(productId, price, productQuality)
                .orElseThrow(() -> new CreamException(ErrorCode.BAD_BUSINESS_LOGIC));
    }
}
