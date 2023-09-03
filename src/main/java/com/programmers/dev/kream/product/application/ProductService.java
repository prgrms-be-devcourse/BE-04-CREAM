package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.product.ui.dto.ProductResponse;
import com.programmers.dev.kream.product.ui.dto.ProductUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final SizedProductRepository sizedProductRepository;

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository, SizedProductRepository sizedProductRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.sizedProductRepository = sizedProductRepository;
    }

    @Transactional
    public Long save(Long brandId, String ProductName, ProductInfo productInfo) {
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드가 존재하지 않습니다."));

        Product product = new Product(brand, ProductName, productInfo);
        Product savedProduct = productRepository.save(product);

        return savedProduct.getId();
    }

    @Transactional
    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));

        sizedProductRepository.findAll()
                .forEach(sizedProduct -> {
                    if (sizedProduct.getProduct() == product) {
                        sizedProductRepository.delete(sizedProduct);
                    }
                });

        productRepository.delete(product);
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        return ProductResponse.fromEntity(product);
    }

    public ProductResponse findByName(String productName) {
        Product product = productRepository.findByName(productName)
            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public void update(Long id, ProductUpdateRequest productUpdateRequest) {
        Product updateProduct = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        Brand brand = brandRepository.findById(productUpdateRequest.brandId())
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드가 존재하지 않습니다."));

        ProductInfo productInfo = new ProductInfo(productUpdateRequest, updateProduct.getProductInfo().getReleaseDate());

        updateProduct.update(brand, productUpdateRequest.productName(), productInfo);
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }
}
