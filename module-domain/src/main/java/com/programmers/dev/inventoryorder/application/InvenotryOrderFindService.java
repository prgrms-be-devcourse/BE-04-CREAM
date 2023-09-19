package com.programmers.dev.inventoryorder.application;

import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.inventoryorder.domain.InventoryOrder;
import com.programmers.dev.inventoryorder.domain.InventoryOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvenotryOrderFindService {

    private final InventoryOrderRepository inventoryOrderRepository;

    public InventoryOrder findByInventoryId(Long inventoryId) {
        return inventoryOrderRepository.findByInventoryId(inventoryId)
                .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));
    }
}
