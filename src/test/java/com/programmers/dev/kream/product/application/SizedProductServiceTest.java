package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.Brand;
import com.programmers.dev.kream.product.domain.BrandRepository;
import com.programmers.dev.kream.product.domain.ProductInfo;
import com.programmers.dev.kream.product.domain.SizedProduct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@SpringBootTest
@Transactional
class SizedProductServiceTest {

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductService productService;

    @Autowired
    SizedProductService sizedProductService;

    @Test
    void saveTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        Long productId = productService.save(savedBrand.getId(), "jordan", productInfo);

        //when
        Long savedSizedProduct = sizedProductService.save(productId, 260);

        //then
        SizedProduct findSizedProduct = sizedProductService.findById(savedSizedProduct);
        Assertions.assertThat(findSizedProduct.getSize()).isEqualTo(260);
    }

    @Test
    void deleteTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);
        
        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        Long productId = productService.save(savedBrand.getId(), "jordan", productInfo);

        Long savedSizedProduct = sizedProductService.save(productId, 260);

        //when
        sizedProductService.delete(savedSizedProduct);

        //then
        Assertions.assertThatThrownBy(() -> sizedProductService.findById(savedSizedProduct))
            .isInstanceOf(NoSuchElementException.class);
    }
}