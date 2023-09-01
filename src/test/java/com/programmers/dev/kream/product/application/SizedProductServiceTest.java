package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.application.SizedProductService;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.product.ui.GetProductInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class SizedProductServiceTest {

    @Autowired
    SizedProductService sizedProductService;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SizedProductRepository sizedProductRepository;

    @Test
    @DisplayName("상품 조회 로직 검증 ")
    void findSizedProduct() {
        // given
        Brand brand = new Brand("nike");
        ProductInfo productInfo = new ProductInfo("model1", LocalDateTime.now(), "RED", 100000L);
        Product product = new Product(brand, "Jordan", productInfo);
        SizedProduct size250 = new SizedProduct(product, 250);
        SizedProduct size260 = new SizedProduct(product, 260);
        SizedProduct size270 = new SizedProduct(product, 270);

        brandRepository.save(brand);
        productRepository.save(product);
        sizedProductRepository.saveAll(List.of(size250, size260, size270));


        // when
        GetProductInfoResponse getProductInfoResponse = sizedProductService.getProductInfo(product.getId()).orElseThrow();
        System.out.println(getProductInfoResponse.sizes());
        // then
        assertAll(
                () -> assertThat(getProductInfoResponse.sizes()).hasSize(3),
                () -> assertThat(getProductInfoResponse.productId()).isEqualTo(product.getId()),
                () -> assertThat(getProductInfoResponse.brandName()).isEqualTo(brand.getName())
        );


    }

}
