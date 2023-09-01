package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.product.ui.ProductUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    public Long save(Long brandId, String ProductName, ProductInfo productInfo) {
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드가 존재하지 않습니다."));

        Product product = new Product(brand, ProductName, productInfo);
        productRepository.save(product);

        return product.getId();
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));
        productRepository.delete(product);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
    }

    public Product findByName(String productName) {
        return productRepository.findByName(productName)
            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
    }

    @Transactional
    public void update(Long id, ProductUpdateRequest productUpdateRequest) {
        Product updateProduct = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        Brand brand = brandRepository.findById(productUpdateRequest.brandId())
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드가 존재하지 않습니다."));

        ProductInfo productInfo = new ProductInfo(
            productUpdateRequest.modelNumber(),
            productUpdateRequest.releaseDate(),
            productUpdateRequest.color(),
            productUpdateRequest.releasePrice());

        updateProduct.updateProduct(brand, productUpdateRequest.productName(), productInfo);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
