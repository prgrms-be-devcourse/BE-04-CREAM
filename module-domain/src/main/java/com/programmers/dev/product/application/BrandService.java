package com.programmers.dev.product.application;

import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.product.domain.Brand;
import com.programmers.dev.product.domain.BrandRepository;
import com.programmers.dev.product.dto.BrandResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.programmers.dev.exception.ErrorCode.INVALID_ID;

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
