package com.programmers.dev.kream.product.ui;

import com.programmers.dev.kream.product.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SizedProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SizedProductRepository sizedProductRepository;


    @Test
    @DisplayName("상품 조회 로직 검증")
    void findSizedProduct() throws Exception {
        //given

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
        ResultActions resultActions = this.mockMvc.perform(
                get("/api/products/{productId}", product.getId())
        );

        // then
        resultActions.andExpect(status().isOk());
    }
}
