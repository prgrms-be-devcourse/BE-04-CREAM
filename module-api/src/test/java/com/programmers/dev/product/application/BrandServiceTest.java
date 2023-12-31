package com.programmers.dev.product.application;

import com.programmers.dev.product.domain.Brand;
import com.programmers.dev.product.domain.BrandRepository;
import com.programmers.dev.product.domain.ProductRepository;
import com.programmers.dev.product.dto.BrandResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BrandServiceTest {

    @Autowired
    BrandService brandService;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("브랜드를 저장할 수 있다")
    void save() {
        //given
        String brandName = "Crocs";

        //when
        BrandResponse savedBrand = brandService.save(brandName);

        //then
        BrandResponse findBrand = brandService.findByName(brandName);
        assertThat(findBrand.id()).isEqualTo(savedBrand.id());
    }

    @Test
    @DisplayName("저장된 브랜드들을 조회할 수 있다")
    void findAll() {
        //given
        Brand brand1 = new Brand("NIKE");
        Brand brand2 = new Brand("ADIDAS");
        brandRepository.save(brand1);
        brandRepository.save(brand2);

        //when
        List<BrandResponse> result = brandService.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Id로 저장된 브랜드를 조회할 수 있다.")
    void findByIdTest() {
        //given
        Brand nike = new Brand("NIKE");
        brandRepository.save(nike);

        //when
        BrandResponse findBrandById = brandService.findById(nike.getId());

        //then
        assertThat(findBrandById.name()).isEqualTo("NIKE");
    }
}
