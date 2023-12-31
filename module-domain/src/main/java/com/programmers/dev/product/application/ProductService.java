package com.programmers.dev.product.application;

import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.product.domain.Brand;
import com.programmers.dev.product.domain.BrandRepository;
import com.programmers.dev.product.domain.Product;
import com.programmers.dev.product.domain.ProductRepository;
import com.programmers.dev.product.dto.*;
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
    public ProductUpdateResponse update(ProductUpdateRequest productUpdateRequest) {
        Brand brand = findBrandById(productUpdateRequest.brandId());

        productRepository.updateProductInfo(
            productUpdateRequest.brandId(),
            productUpdateRequest.productName(),
            productUpdateRequest.color(),
            productUpdateRequest.modelNumber());

        return new ProductUpdateResponse(
            brand.getName(),
            productUpdateRequest.productName(),
            productUpdateRequest.color(),
            productUpdateRequest.modelNumber()
        );
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
