package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.Brand;
import com.programmers.dev.kream.product.domain.BrandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BrandServiceTest {

    @Autowired
    BrandService brandService;

    @Autowired
    BrandRepository brandRepository;

    @Test
    @DisplayName("브랜드를 저장할 수 있다")
    void save() {
        //given
        String brandName = "Crocs";

        //when
        Long savedId = brandService.save(brandName);

        //then
        Brand findBrand = brandService.findByName(brandName);
        assertThat(findBrand.getId()).isEqualTo(savedId);
    }

    @Test
    @DisplayName("브랜드를 삭제할 수 있다")
    void delete() {
        //given
        String brandName = "Crocs";

        brandService.save(brandName);

        //when
        brandService.delete(brandName);

        //then
        assertThatThrownBy(() -> brandService.findByName(brandName)).isInstanceOf(NoSuchElementException.class);
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
        List<Brand> result = brandService.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
    }
}
