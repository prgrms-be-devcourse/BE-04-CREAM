package com.programmers.dev.inventory.ui;


import com.programmers.dev.inventory.application.InventoryStateChangeService;
import com.programmers.dev.inventory.dto.statechange.InventoryAuthenticateFailRequest;
import com.programmers.dev.inventory.dto.statechange.InventoryAuthenticatePassRequest;
import com.programmers.dev.inventory.dto.statechange.InventoryArrivedRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/inventories/state-change")
@RequiredArgsConstructor
public class InventoryStateChangeController {

    private final InventoryStateChangeService inventoryStateChangeService;

    @PostMapping("/arrived")
    public ResponseEntity<String> warehouseArrived(@RequestBody InventoryArrivedRequest request) {
        inventoryStateChangeService.warehouseArrived(request);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/authentication/{inventoryId}/passed")
    public ResponseEntity<String> authenticatePassed(@PathVariable Long inventoryId, @RequestBody InventoryAuthenticatePassRequest request) {
        inventoryStateChangeService.authenticatePassed(inventoryId, request);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/authentication/{inventoryId}/failed")
    public ResponseEntity<String> authenticateFailed(@PathVariable Long inventoryId, @RequestBody InventoryAuthenticateFailRequest request) {
        inventoryStateChangeService.authenticateFailed(inventoryId, request);

        return ResponseEntity.ok("success");
    }
}
