package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.exception.CreamException;
import com.programmers.dev.kream.exception.ErrorCode;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.product.ui.dto.BrandResponse;
import com.programmers.dev.kream.product.ui.dto.ProductResponse;
import com.programmers.dev.kream.product.ui.dto.ProductSaveRequest;
import com.programmers.dev.kream.product.ui.dto.ProductUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    @Transactional
    public ProductResponse save(ProductSaveRequest productSaveRequest) {
        Brand brand = findBrandById(productSaveRequest.brandId());

        Product product = new Product(brand, productSaveRequest.name(), productSaveRequest.productInfo(), productSaveRequest.size());
        Product savedProduct = productRepository.save(product);

        return new ProductResponse(
            savedProduct.getId(),
            new BrandResponse(savedProduct.getBrand().getId(), savedProduct.getBrand().getName()),
            savedProduct.getName(),
            savedProduct.getProductInfo());
    }

    public ProductResponse findById(Long id) {
        Product product = findProductById(id);

        return ProductResponse.fromEntity(product);
    }

    public ProductResponse findByName(String productName) {
        Product product = productRepository.findByName(productName)
            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest productUpdateRequest) {
        Product updateProduct = findProductById(id);

        Brand brand = findBrandById(productUpdateRequest.brandId());

        ProductInfo productInfo = new ProductInfo(productUpdateRequest, updateProduct.getProductInfo().getReleaseDate());

        updateProduct.update(brand, productUpdateRequest.productName(), productInfo);

        return new ProductResponse(
            id,
            new BrandResponse(brand.getId(), brand.getName()),
            productUpdateRequest.productName(),
            productInfo);
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    private Brand findBrandById(Long id) {
        return brandRepository.findById(id)
            .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));
    }
}
