package com.programmers.dev.inventory.ui;


import com.programmers.dev.inventory.application.InventoryStateChangeService;
import com.programmers.dev.inventory.dto.statechange.InventoryAuthenticateFailRequest;
import com.programmers.dev.inventory.dto.statechange.InventoryAuthenticatePassRequest;
import com.programmers.dev.inventory.dto.statechange.InventoryRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/inventories/state-change")
@RequiredArgsConstructor
public class InventoryStateChangeController {

    private final InventoryStateChangeService inventoryStateChangeService;

    @PostMapping("/arrived")
    public ResponseEntity<String> warehouseArrived(@RequestBody InventoryRegisterRequest request) {
        inventoryStateChangeService.warehouseArrived(request);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/authentication/{inventoryId}/pass")
    public ResponseEntity<String> authenticatePass(@PathVariable Long inventoryId, @RequestBody InventoryAuthenticatePassRequest request) {
        inventoryStateChangeService.authenticatePass(inventoryId, request);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/authentication/{inventoryId}/fail")
    public ResponseEntity<String> authenticateFail(@PathVariable Long inventoryId, @RequestBody InventoryAuthenticateFailRequest request) {
        inventoryStateChangeService.authenticateFail(inventoryId, request);

        return ResponseEntity.ok("success");
    }
}
