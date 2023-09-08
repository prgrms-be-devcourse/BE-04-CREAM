package com.programmers.dev.kream.product.application;


import com.programmers.dev.kream.exception.CreamException;
import com.programmers.dev.kream.exception.ErrorCode;
import com.programmers.dev.kream.product.domain.Product;
import com.programmers.dev.kream.product.domain.ProductRepository;
import com.programmers.dev.kream.product.ui.dto.GetProductInfoResponse;
import com.programmers.dev.kream.product.ui.dto.SizedProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        Product product = findProductById(productId);

        SizedProduct sizedProduct = new SizedProduct(product, size);
        SizedProduct savedSizedProduct = sizedProductRepository.save(sizedProduct);

        return savedSizedProduct.getId();
    }

    @Transactional
    public void deleteById(Long id) {
        SizedProduct sizedProduct = findSizedProductById(id);

        sizedProductRepository.delete(sizedProduct);
    }

    public SizedProductResponse findById(Long id) {
        SizedProduct sizedProduct = findSizedProductById(id);

        return SizedProductResponse.fromEntity(sizedProduct);
    }

    public Optional<GetProductInfoResponse> getProductInfo(Long productId) {
        List<SizedProduct> sizedProductList = sizedProductRepository.findAllByProductId(productId);

        return GetProductInfoResponse.of(sizedProductList);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));
    }

    private SizedProduct findSizedProductById(Long id) {
        return sizedProductRepository.findById(id)
            .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));
    }
}
