package com.programmers.dev.kream.product.ui;

import com.programmers.dev.kream.product.application.ProductService;
import com.programmers.dev.kream.product.ui.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ProductsGetResponse> getProducts() {
        List<ProductResponse> productList = productService.findAll();

        return ResponseEntity.ok(new ProductsGetResponse(productList.size(), productList));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductSaveRequest productSaveRequest) {
        ProductResponse savedProductResponse = productService.save(productSaveRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(savedProductResponse);
    }

    @PostMapping("/update")
    public ResponseEntity<ProductUpdateResponse> updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest) {
        ProductUpdateResponse productUpdateResponse = productService.update(productUpdateRequest);

        return ResponseEntity.ok().body(productUpdateResponse);
    }
}
