package com.programmers.dev.inventory.ui;


import com.programmers.dev.inventory.application.InventoryStoreService;
import com.programmers.dev.inventory.dto.InventoryStoreRequest;
import com.programmers.dev.inventory.dto.InventoryStoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryStoreService inventoryStoreService;

    @PostMapping("/store")
    public ResponseEntity<InventoryStoreResponse> store(@AuthenticationPrincipal Long userId,
                                                        @RequestBody @Validated InventoryStoreRequest request) {
        InventoryStoreResponse response = inventoryStoreService.store(userId, request);

        return ResponseEntity.ok(response);
    }
}
