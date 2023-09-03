package com.programmers.dev.kream.product.ui;

import com.programmers.dev.kream.product.application.ProductService;
import com.programmers.dev.kream.product.ui.dto.ListResponse;
import com.programmers.dev.kream.product.ui.dto.ProductResponse;
import com.programmers.dev.kream.product.ui.dto.ProductSaveRequest;
import com.programmers.dev.kream.product.ui.dto.ProductUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ListResponse<ProductResponse>> getProducts() {
        List<ProductResponse> productList = productService.findAll();

        return ResponseEntity.ok(new ListResponse<>(productList.size(), productList));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> saveProduct(@RequestBody ProductSaveRequest productSaveRequest) {
        productService.save(
            productSaveRequest.brandId(),
            productSaveRequest.name(),
            productSaveRequest.productInfo());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Collections.singletonMap("productName", productSaveRequest.name()));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Long> deleteProduct(@PathVariable Long productId) {
        productService.deleteById(productId);

        return ResponseEntity.ok().body(productId);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId, @RequestBody ProductUpdateRequest productUpdateRequest) {
        productService.update(productId, productUpdateRequest);

        return ResponseEntity.ok().body(productUpdateRequest.productName());
    }

}
