package com.programmers.dev.inventory.ui;


import com.programmers.dev.inventory.application.InventoryStateChangeService;
import com.programmers.dev.inventory.dto.statechange.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/inventories/state-change")
@RequiredArgsConstructor
public class InventoryStateChangeController {

    private final InventoryStateChangeService inventoryStateChangeService;

    @PostMapping("/arrived")
    public ResponseEntity<InventoryMultipleStateChangeResponse> warehouseArrived(@RequestBody InventoryArrivedRequest request) {
        inventoryStateChangeService.warehouseArrived(request);

        return ResponseEntity.ok(new InventoryMultipleStateChangeResponse(request.inventoryIds()));
    }

    @PostMapping("/authentication/{inventoryId}/passed")
    public ResponseEntity<InventorySingleStateChangeResponse> authenticatePassed(@PathVariable Long inventoryId, @RequestBody InventoryAuthenticatePassRequest request) {
        inventoryStateChangeService.authenticatePassed(inventoryId, request);

        return ResponseEntity.ok(new InventorySingleStateChangeResponse(inventoryId));
    }

    @PostMapping("/authentication/{inventoryId}/failed")
    public ResponseEntity<InventorySingleStateChangeResponse> authenticateFailed(@PathVariable Long inventoryId, @RequestBody InventoryAuthenticateFailRequest request) {
        inventoryStateChangeService.authenticateFailed(inventoryId, request);

        return ResponseEntity.ok(new InventorySingleStateChangeResponse(inventoryId));
    }

    @PostMapping("/{inventoryId}/set-price")
    public ResponseEntity<InventorySingleStateChangeResponse> setPrice(@PathVariable Long inventoryId, @RequestBody InventorySetPriceRequest request) {
        inventoryStateChangeService.setPrice(inventoryId, request);

        return ResponseEntity.ok(new InventorySingleStateChangeResponse(inventoryId));
    }

    @PostMapping("/{inventoryId}/finished")
    public ResponseEntity<InventorySingleStateChangeResponse> finished(@PathVariable Long inventoryId) {
        inventoryStateChangeService.finished(inventoryId);

        return ResponseEntity.ok(new InventorySingleStateChangeResponse(inventoryId));
    }
}
