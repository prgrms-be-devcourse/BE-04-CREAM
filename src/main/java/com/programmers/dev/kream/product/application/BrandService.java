package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.Brand;
import com.programmers.dev.kream.product.domain.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Long save(String name) {
        Brand brand = new Brand(name);
        brandRepository.save(brand);

        return brand.getId();
    }

    @Transactional
    public void delete(String name) {
        Brand brand = brandRepository.findByName(name)
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드가 존재하지 않습니다."));

        brandRepository.delete(brand);
    }

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    public Brand findByName(String name) {
        return brandRepository.findByName(name)
            .orElseThrow(() -> new NoSuchElementException("해당 브랜드는 존재하지 않습니다."));
    }
}
