package com.programmers.dev.kream.product.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.product.application.ProductService;
import com.programmers.dev.kream.product.domain.ProductInfo;
import com.programmers.dev.kream.product.ui.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProductService productService;

    @Test
    @DisplayName("상품들을 조회할 수 있다.")
    void getProductsTest() throws Exception {
        //given
        BrandResponse nikeResponse = new BrandResponse(1L, "NIKE");
        BrandResponse adidasResponse = new BrandResponse(2L, "ADIDAS");

        ProductInfo nikeInfo = new ProductInfo("NIKE_1", LocalDateTime.now(), "RED", 10000L);
        ProductInfo adidasInfo = new ProductInfo("ADIDAS_1", LocalDateTime.now(), "WHITE", 20000L);

        List<ProductResponse> productResponses = List.of(
            new ProductResponse(1L, nikeResponse, "airForce", nikeInfo),
            new ProductResponse(2L, adidasResponse, "stanSmith", adidasInfo));

        given(productService.findAll()).willReturn(
            new ProductsGetResponse(productResponses.size(), productResponses).productList()
        );

        //when & then
        mockMvc.perform(
                get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.productList[0].name").value("airForce"));

        verify(productService).findAll();
    }

    @Test
    @DisplayName("상품을 저장할 수 있다.")
    void saveProductTest() throws Exception {
        //given
        BrandResponse nikeResponse = new BrandResponse(1L, "NIKE");
        ProductInfo nikeInfo = new ProductInfo("NIKE_1", LocalDateTime.now(), "RED", 10000L);
        ProductSaveRequest productSaveRequest = new ProductSaveRequest(1L, "airForce", nikeInfo);

        given(productService.save(any(productSaveRequest.getClass()))).willReturn(
            new ProductResponse(1L, nikeResponse, "airForce", nikeInfo)
        );

        String requestBody = objectMapper.writeValueAsString(productSaveRequest);

        //when & then
        mockMvc.perform(
                post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andDo(print());
    }

    @Test
    @DisplayName("id로 상품을 수정할 수 있다.")
    void updateProductTest() throws Exception {
        //given
        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            1L,
            "airForce",
            "AAA",
            "Blue",
            3000L);

        given(productService.update(1L, productUpdateRequest)).willReturn(
            new ProductResponse(
                1L,
                new BrandResponse(1L, "NIKE"),
                "airForce",
                new ProductInfo(
                    "AAA",
                    LocalDateTime.now(),
                    "Blue",
                    3000L
                )
            )
        );

        String requestBody = objectMapper.writeValueAsString(productUpdateRequest);

        //when & then
        mockMvc.perform(
                post("/api/products/{productId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("airForce"))
            .andDo(print());
    }
}