package com.programmers.dev.kream.product.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.product.ui.dto.SizedProductSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SizedProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
            get("/api/sizedproducts/{productId}", product.getId())
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("사이즈가 있는 상품을 저장할 수 있다.")
    void saveSizedProductTest() throws Exception {
        //given
        Brand brand = new Brand("nike");
        brandRepository.save(brand);

        ProductInfo productInfo = new ProductInfo("model1", LocalDateTime.now(), "RED", 100000L);
        Product product = new Product(brand, "Jordan", productInfo);
        productRepository.save(product);

        SizedProductSaveRequest sized250 = new SizedProductSaveRequest(250);

        //when
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/sizedproducts/{productId}", product.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(sized250))
        );

        //then
        resultActions.andExpect(status().isCreated())
            .andDo(print());
    }

    @Test
    @DisplayName("id로 상품의 사이즈를 삭제할 수 있다.")
    void deleteSizedProductTest() throws Exception {
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

        //when
        ResultActions resultActions = this.mockMvc.perform(
            delete("/api/sizedproducts/{sizedProductId}", size250.getId())
        );

        //then
        resultActions.andExpect(status().isOk())
            .andDo(print());
    }
}
