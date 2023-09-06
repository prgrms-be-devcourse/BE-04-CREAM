package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.exception.CreamException;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.product.ui.dto.ProductResponse;
import com.programmers.dev.kream.product.ui.dto.ProductSaveRequest;
import com.programmers.dev.kream.product.ui.dto.SizedProductResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@SpringBootTest
@Transactional
class SizedProductServiceTest {

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SizedProductRepository sizedProductRepository;

    @Autowired
    ProductService productService;

    @Autowired
    SizedProductService sizedProductService;

    @Test
    @DisplayName("사이즈가 있는 상품을 저장할 수 있다")
    void saveTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);


        ProductResponse savedProductResponse = productService.save(new ProductSaveRequest(
            savedBrand.getId(),
            "jordan",
            productInfo));

        //when
        Long savedSizedProduct = sizedProductService.save(savedProductResponse.id(), 260);

        //then
        SizedProductResponse findSizedProduct = sizedProductService.findById(savedSizedProduct);
        Assertions.assertThat(findSizedProduct.size()).isEqualTo(260);
    }

    @Test
    @DisplayName("사이즈가 있는 상품을 삭제할 수 있다")
    void deleteTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        ProductResponse savedProductResponse = productService.save(new ProductSaveRequest(
            savedBrand.getId(),
            "jordan",
            productInfo));

        Long savedSizedProduct = sizedProductService.save(savedProductResponse.id(), 260);

        //when
        sizedProductService.deleteById(savedSizedProduct);

        //then
        Assertions.assertThatThrownBy(() -> sizedProductService.findById(savedSizedProduct))
            .isInstanceOf(CreamException.class);
    }
}

