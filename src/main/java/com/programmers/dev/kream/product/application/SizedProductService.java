package com.programmers.dev.kream.product.application;


import com.programmers.dev.kream.product.domain.Product;
import com.programmers.dev.kream.product.domain.ProductRepository;
import com.programmers.dev.kream.product.domain.SizedProduct;
import com.programmers.dev.kream.product.domain.SizedProductRepository;
import com.programmers.dev.kream.product.ui.dto.GetProductInfoResponse;
import com.programmers.dev.kream.product.ui.dto.SizedProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SizedProductService {

    private final SizedProductRepository sizedProductRepository;
    private final ProductRepository productRepository;

    public SizedProductService(SizedProductRepository sizedProductRepository, ProductRepository productRepository) {
        this.sizedProductRepository = sizedProductRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Long save(Long productId, int size) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NoSuchElementException("해당 제품이 존재하지 않습니다."));

        SizedProduct sizedProduct = new SizedProduct(product, size);
        SizedProduct savedSizedProduct = sizedProductRepository.save(sizedProduct);

        return savedSizedProduct.getId();
    }

    @Transactional
    public void deleteById(Long id) {
        SizedProduct sizedProduct = sizedProductRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 사이즈의 상품은 존재하지 않습니다."));

        sizedProductRepository.delete(sizedProduct);
    }

    public SizedProductResponse findById(Long id) {
        SizedProduct sizedProduct = sizedProductRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 사이즈의 상품은 존재하지 않습니다."));

        return SizedProductResponse.fromEntity(sizedProduct);
    }

    public Optional<GetProductInfoResponse> getProductInfo(Long productId) {
        List<SizedProduct> sizedProductList = sizedProductRepository.findAllByProductId(productId);

        return GetProductInfoResponse.of(sizedProductList);
    }
}
