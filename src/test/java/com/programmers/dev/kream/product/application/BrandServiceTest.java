package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.Brand;
import com.programmers.dev.kream.product.domain.BrandRepository;
import com.programmers.dev.kream.product.ui.dto.BrandResponse;
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
        BrandResponse findBrand = brandService.findByName(brandName);
        assertThat(findBrand.id()).isEqualTo(savedId);
    }

    @Test
    @DisplayName("브랜드를 삭제할 수 있다")
    void delete() {
        //given
        String brandName = "Crocs";

        brandService.save(brandName);

        //when
        brandService.deleteByName(brandName);

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

    @Test
    @DisplayName("Id로 저장된 브랜드를 삭제할 수 있다.")
    void deleteByIdTest() {
        //given
        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        BrandResponse findBrand = brandService.findById(savedBrand.getId());

        //when
        brandService.deleteById(findBrand.id());

        //then
        assertThatThrownBy(() -> brandService.findById(findBrand.id()))
            .isInstanceOf(NoSuchElementException.class);
    }
}
