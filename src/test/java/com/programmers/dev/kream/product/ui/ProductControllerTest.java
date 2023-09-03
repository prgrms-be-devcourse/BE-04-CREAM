package com.programmers.dev.kream.product.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.product.ui.dto.ProductSaveRequest;
import com.programmers.dev.kream.product.ui.dto.ProductUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BrandRepository brandRepository;

    @Test
    @DisplayName("상품들을 조회할 수 있다.")
    void getProductsTest() throws Exception {
        //given
        Brand nike = new Brand("NIKE");
        ProductInfo airForceInfo = new ProductInfo("AAA", LocalDateTime.now(), "WHITE", 100000L);
        ProductInfo dunkInfo = new ProductInfo("BBB", LocalDateTime.now(), "BLACK", 50000L);
        Product airForce = new Product(nike, "AIR_FORCE", airForceInfo);
        Product dunkLow = new Product(nike, "DUNK_LOW", dunkInfo);

        brandRepository.save(nike);
        productRepository.saveAll(List.of(airForce, dunkLow));

        //when
        ResultActions resultActions = this.mockMvc.perform(
            get("/api/products")
        );

        //then
        resultActions.andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("상품을 저장할 수 있다.")
    void saveProductTest() throws Exception {
        //given
        Brand nike = new Brand("NIKE");
        brandRepository.save(nike);

        ProductInfo airForceInfo = new ProductInfo("AAA", LocalDateTime.now(), "WHITE", 100000L);
        ProductSaveRequest productSaveRequest = new ProductSaveRequest(nike.getId(), "AIR_FORCE", airForceInfo);


        //when
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/products")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(productSaveRequest))
        );

        //then
        resultActions.andExpect(status().isCreated())
            .andDo(print());
    }

    @Test
    @DisplayName("id로 상품을 삭제할 수 있다.")
    void deleteProductTest() throws Exception {
        //given
        Brand nike = new Brand("NIKE");
        brandRepository.save(nike);

        ProductInfo airForceInfo = new ProductInfo("AAA", LocalDateTime.now(), "WHITE", 100000L);
        Product airForce = new Product(nike, "AIR_FORCE", airForceInfo);
        Product savedProduct = productRepository.save(airForce);

        //then
        ResultActions resultActions = this.mockMvc.perform(
            delete("/api/products/{productId}", savedProduct.getId())
        );

        //then
        resultActions.andExpect(status().isOk())
            .andDo(print());

    }

    @Test
    @DisplayName("id로 상품을 수정할 수 있다.")
    void updateProductTest() throws Exception {
        //given
        Brand nike = new Brand("NIKE");
        brandRepository.save(nike);

        ProductInfo airForceInfo = new ProductInfo("AAA", LocalDateTime.now(), "WHITE", 100000L);
        Product airForce = new Product(nike, "AIR_FORCE", airForceInfo);
        Product savedProduct = productRepository.save(airForce);

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(nike.getId(), "WALKKER", "BBB", "BLACK", 30000L);

        //then
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/products/{productId}", savedProduct.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(productUpdateRequest))
        );

        //then
        resultActions.andExpect(status().isOk())
            .andDo(print());
    }

}