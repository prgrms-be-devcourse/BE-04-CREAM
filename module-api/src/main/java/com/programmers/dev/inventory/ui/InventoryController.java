package com.programmers.dev.inventory.ui;


import com.programmers.dev.inventory.application.InventoryOrderService;
import com.programmers.dev.inventory.application.InventoryRegisterService;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.dto.InventoryOrderRequest;
import com.programmers.dev.inventory.dto.InventoryOrderResponse;
import com.programmers.dev.inventory.dto.InventoryRegisterRequest;
import com.programmers.dev.inventory.dto.InventoryRegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryRegisterService inventoryRegisterService;

    private final InventoryOrderService inventoryOrderService;

    @PostMapping("/register")
    public ResponseEntity<InventoryRegisterResponse> register(@AuthenticationPrincipal Long userId,
                                                              @RequestBody @Validated InventoryRegisterRequest request) {
        InventoryRegisterResponse response = inventoryRegisterService.register(userId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/order")
    public ResponseEntity<InventoryOrderResponse> order(@AuthenticationPrincipal Long userId,
                                                        @RequestBody @Validated InventoryOrderRequest request,
                                                        @RequestParam(required = true) Inventory.ProductQuality productQuality
    ) {
        InventoryOrderResponse response = inventoryOrderService.order(userId, request, productQuality);

        return ResponseEntity.ok(response);
    }
}
