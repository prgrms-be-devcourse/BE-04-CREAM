package com.programmers.dev.kream.product.ui;

import com.programmers.dev.kream.product.application.BrandService;
import com.programmers.dev.kream.product.ui.dto.BrandSaveRequest;
import com.programmers.dev.kream.product.ui.dto.BrandResponse;
import com.programmers.dev.kream.product.ui.dto.ListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<BrandResponse> getBrandInfo(@PathVariable Long brandId) {
        BrandResponse findBrandById = brandService.findById(brandId);

        return ResponseEntity.ok(findBrandById);
    }

    @GetMapping
    public ResponseEntity<ListResponse<BrandResponse>> getBrandsInfo() {
        List<BrandResponse> brandList = brandService.findAll();

        return ResponseEntity.ok(new ListResponse<>(brandList.size(), brandList));
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<Long> deleteBrandInfo(@PathVariable Long brandId) {
        brandService.deleteById(brandId);

        return ResponseEntity.ok().body(brandId);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> saveBrand(@RequestBody BrandSaveRequest brandSaveRequest) {
        brandService.save(brandSaveRequest.name());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Collections.singletonMap("brandName", brandSaveRequest.name()));
    }
}
