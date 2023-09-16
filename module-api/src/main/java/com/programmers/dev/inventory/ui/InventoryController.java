package com.programmers.dev.inventory.ui;


import com.programmers.dev.inventory.application.InventoryRegisterService;
import com.programmers.dev.inventory.dto.InventoryRegisterRequest;
import com.programmers.dev.inventory.dto.InventoryRegisterResponse;
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

    private final InventoryRegisterService inventoryRegisterService;

    @PostMapping("/register")
    public ResponseEntity<InventoryRegisterResponse> register(@AuthenticationPrincipal Long userId,
                                                              @RequestBody @Validated InventoryRegisterRequest request) {
        InventoryRegisterResponse response = inventoryRegisterService.register(userId, request);

        return ResponseEntity.ok(response);
    }
}
