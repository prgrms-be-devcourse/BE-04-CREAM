package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.exception.CreamException;
import com.programmers.dev.kream.exception.ErrorCode;
import com.programmers.dev.kream.product.domain.Brand;
import com.programmers.dev.kream.product.domain.BrandRepository;
import com.programmers.dev.kream.product.ui.dto.BrandResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.programmers.dev.kream.exception.ErrorCode.INVALID_ID;

@Service
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Transactional
    public BrandResponse save(String name) {
        Brand brand = new Brand(name);
        Brand savedBrand = brandRepository.save(brand);

        return new BrandResponse(savedBrand.getId(), savedBrand.getName());
    }
    public List<BrandResponse> findAll() {
        return brandRepository.findAll().stream()
            .map(BrandResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public BrandResponse findByName(String name) {
        Brand brand = findBrandByName(name);

        return BrandResponse.fromEntity(brand);
    }

    public BrandResponse findById(Long id) {
        Brand brand = findBrandById(id);

        return BrandResponse.fromEntity(brand);
    }

    private Brand findBrandById(Long id) {
        return brandRepository.findById(id)
            .orElseThrow(() -> new CreamException(INVALID_ID));
    }

    private Brand findBrandByName(String name) {
        return brandRepository.findByName(name)
            .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));
    }
}
