package com.programmers.dev.inventory.ui;


import com.programmers.dev.inventory.application.InventoryStateChangeService;
import com.programmers.dev.inventory.dto.state.InventoryStoreRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/inventories/state-change")
@RequiredArgsConstructor
public class InventoryStateChangeController {

    private final InventoryStateChangeService inventoryStateChangeService;

    @PostMapping("/arrive")
    public ResponseEntity<String> store(@RequestBody @Validated InventoryStoreRequest request) {
        inventoryStateChangeService.store(request);

        return ResponseEntity.ok("success");
    }
}
