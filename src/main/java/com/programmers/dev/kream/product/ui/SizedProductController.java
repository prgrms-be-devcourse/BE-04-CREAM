package com.programmers.dev.kream.product.ui;

import com.programmers.dev.kream.product.application.SizedProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
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
}
