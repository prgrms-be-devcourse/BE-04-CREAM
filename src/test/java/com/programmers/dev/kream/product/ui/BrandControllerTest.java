package com.programmers.dev.kream.product.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.product.application.BrandService;
import com.programmers.dev.kream.product.ui.dto.BrandResponse;
import com.programmers.dev.kream.product.ui.dto.BrandSaveRequest;
import com.programmers.dev.kream.product.ui.dto.BrandsGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BrandService brandService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("브랜드 등록을 할 수 있다.")
    void saveBrand() throws Exception {
        //given
        given(brandService.save("NIKE")).willReturn(
            new BrandResponse(1L, "NIKE")
        );

        String requestBody = objectMapper.writeValueAsString(new BrandSaveRequest("NIKE"));

        //when & then
        mockMvc.perform(
                post("/api/brands")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("NIKE"));

        verify(brandService).save("NIKE");
    }

    @Test
    @DisplayName("id로 브랜드를 조회할 수 있다.")
    void getBrandInfoTest() throws Exception {
        //given
        given(brandService.findById(1L)).willReturn(
            new BrandResponse(1L, "NIKE")
        );

        //when & then
        mockMvc.perform(
                get("/api/brands/{brandId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("NIKE"))
            .andDo(print());

        verify(brandService).findById(1L);
    }

    @Test
    @DisplayName("모든 브랜드를 조회할 수 있다.")
    void getBrandsInfoTest() throws Exception {
        //given
        List<BrandResponse> brandResponses = List.of(
            new BrandResponse(1L, "NIKE"),
            new BrandResponse(2L, "ADIDAS"));

        given(brandService.findAll()).willReturn(
            new BrandsGetResponse(brandResponses.size(), brandResponses).brandList()
        );

        //when & then
        mockMvc.perform(
                get("/api/brands"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.brandList[0].name").value("NIKE"))
            .andDo(print());

        verify(brandService).findAll();
    }
}