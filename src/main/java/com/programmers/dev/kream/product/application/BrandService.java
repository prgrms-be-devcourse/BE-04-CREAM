package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.product.ui.dto.BrandResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final SizedProductRepository sizedProductRepository;

    public BrandService(BrandRepository brandRepository, ProductRepository productRepository, SizedProductRepository sizedProductRepository) {
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
        this.sizedProductRepository = sizedProductRepository;
    }

    @Transactional
    public Long save(String name) {
        Brand brand = new Brand(name);
        Brand savedBrand = brandRepository.save(brand);

        return savedBrand.getId();
    }

    @Transactional
    public void deleteByName(String name) {
        Brand brand = brandRepository.findByName(name)
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드가 존재하지 않습니다."));

        brandRepository.delete(brand);
    }

    @Transactional
    public void deleteById(Long id) {
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드가 존재하지 않습니다."));

        sizedProductRepository.findAll()
                    .forEach(sizedProduct -> {
                        if (sizedProduct.getProduct().getBrand() == brand) {
                            sizedProductRepository.delete(sizedProduct);
                        }
                    });

        productRepository.findAll()
                    .forEach(product -> {
                        if (product.getBrand() == brand) {
                            productRepository.delete(product);
                        }
                    });

        brandRepository.delete(brand);
    }

    public List<BrandResponse> findAll() {
        return brandRepository.findAll().stream()
            .map(BrandResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public BrandResponse findByName(String name) {
        Brand brand = brandRepository.findByName(name)
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드는 존재하지 않습니다."));

        return BrandResponse.fromEntity(brand);
    }

    public BrandResponse findById(Long id) {
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드는 존재하지 않습니다."));

        return BrandResponse.fromEntity(brand);
    }
}
