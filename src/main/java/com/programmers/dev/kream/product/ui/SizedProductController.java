package com.programmers.dev.kream.product.ui;

import com.programmers.dev.kream.product.application.SizedProductService;
import com.programmers.dev.kream.product.ui.dto.GetProductInfoResponse;
import com.programmers.dev.kream.product.ui.dto.SizedProductSaveRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sizedproducts")
public class SizedProductController {

    private final SizedProductService sizedProductService;

    public SizedProductController(SizedProductService sizedProductService) {
        this.sizedProductService = sizedProductService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Optional<GetProductInfoResponse>> getProductInfo(@PathVariable Long productId) {
        Optional<GetProductInfoResponse> sizedProduct = sizedProductService.getProductInfo(productId);

        return ResponseEntity.ok(sizedProduct);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Long>> saveSizedProduct(
        @PathVariable Long productId,
        @RequestBody SizedProductSaveRequest sizedProductSaveRequest) {
        Long savedId = sizedProductService.save(productId, sizedProductSaveRequest.size());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Collections.singletonMap("sizedProductId", savedId));
    }

    @DeleteMapping("/{sizedProductId}")
    public ResponseEntity<Map<String, Long>> deleteSizedProduct(@PathVariable Long sizedProductId) {
        sizedProductService.deleteById(sizedProductId);

        return ResponseEntity.ok().body(Collections.singletonMap("sizedProductId", sizedProductId));
    }
}
