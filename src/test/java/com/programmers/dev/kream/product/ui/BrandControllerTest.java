package com.programmers.dev.kream.product.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.product.domain.Brand;
import com.programmers.dev.kream.product.domain.BrandRepository;
import com.programmers.dev.kream.product.ui.dto.BrandSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BrandRepository brandRepository;

    @Test
    @DisplayName("브랜드 등록을 할 수 있다.")
    void saveBrand() throws Exception {
        //given
        BrandSaveRequest brandSaveRequest = new BrandSaveRequest("NIKE");

        //when
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/brands")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(brandSaveRequest))
        );

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("id로 브랜드를 조회할 수 있다.")
    void getBrandInfoTest() throws Exception {
        //given
        Brand brand = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(brand);

        //when
        ResultActions resultActions = this.mockMvc.perform(
            get("/api/brands/{brandId}", savedBrand.getId())
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("모든 브랜드를 조회할 수 있다.")
    void getBrandsInfoTest() throws Exception {
        //given
        Brand nike = new Brand("NIKE");
        Brand adidas = new Brand("ADIDAS");
        brandRepository.save(nike);
        brandRepository.save(adidas);

        //when
        ResultActions resultActions = this.mockMvc.perform(
            get("/api/brands")
        );

        //then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.size").exists());
    }

    @Test
    @DisplayName("id로 브랜드를 삭제할 수 있다.")
    void deleteBrandInfoTest() throws Exception{
        //given
        Brand nike = new Brand("NIKE");
        Brand adidas = new Brand("ADIDAS");
        Brand brandNike = brandRepository.save(nike);
        brandRepository.save(adidas);

        //when
        ResultActions resultActions = this.mockMvc.perform(
            delete("/api/brands/{brandId}",brandNike.getId())
        );

        //then
        resultActions.andExpect(status().isOk());
    }
}