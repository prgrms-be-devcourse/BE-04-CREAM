package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.Brand;
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

    @Test
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
    void findAll() {
        //given

        //when
        List<Brand> result = brandService.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
    }
}