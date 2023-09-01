package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.SizedProduct;
import com.programmers.dev.kream.product.domain.SizedProductRepository;
import com.programmers.dev.kream.product.ui.GetProductInfoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SizedProductService {

    private final SizedProductRepository sizedProductRepository;

    public SizedProductService(SizedProductRepository sizedProductRepository) {
        this.sizedProductRepository = sizedProductRepository;
    }

    public Optional<GetProductInfoResponse> getProductInfo(Long productId) {
        List<SizedProduct> sizedProductList = sizedProductRepository.findAllByProductId(productId);
        return GetProductInfoResponse.of(sizedProductList);
    }
}
