package com.programmers.dev.kream.product.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.product.application.BrandService;
import com.programmers.dev.kream.product.ui.dto.BrandResponse;
import com.programmers.dev.kream.product.ui.dto.BrandSaveRequest;
import com.programmers.dev.kream.product.ui.dto.BrandsGetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BrandService brandService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup(
        WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentationContextProvider
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .apply(documentationConfiguration(restDocumentationContextProvider)
                .operationPreprocessors()
                .withRequestDefaults(modifyUris().host("13.125.254.94"), prettyPrint())
                .withResponseDefaults(modifyUris().host("13.125.254.94"), prettyPrint()))
            .build();
    }

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
            .andExpect(jsonPath("$.name").value("NIKE"))
            .andDo(document("save-brand",
                requestHeaders(
                    headerWithName(CONTENT_TYPE).description("content type")
                ),
                requestFields(
                    fieldWithPath("name").description("name of brand").type(JsonFieldType.STRING)
                ),
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type")
                ),
                responseFields(
                    fieldWithPath("id").description("id of brand").type(JsonFieldType.NUMBER),
                    fieldWithPath("name").description("name of brand").type(JsonFieldType.STRING)
                )
            ));

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
            .andDo(print())
            .andDo(document("find-brand-by-id",
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type")
                ),
                responseFields(
                    fieldWithPath("id").description("id of brand").type(JsonFieldType.NUMBER),
                    fieldWithPath("name").description("name of brand").type(JsonFieldType.STRING)
                )
            ));

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
            .andDo(print())
            .andDo(document("find-all-brands",
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type")
                ),
                responseFields(
                    fieldWithPath("size").description("count of brands").type(JsonFieldType.NUMBER),
                    fieldWithPath("brandList[].id").description("id of brand").type(JsonFieldType.NUMBER),
                    fieldWithPath("brandList[].name").description("name of brand").type(JsonFieldType.STRING)
                )
            ));

        verify(brandService).findAll();
    }
}