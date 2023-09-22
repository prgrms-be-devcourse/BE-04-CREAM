package com.programmers.dev.product.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.product.application.ProductService;
import com.programmers.dev.product.domain.ProductInfo;
import com.programmers.dev.product.dto.*;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProductService productService;

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
            .andExpect(jsonPath("$.productList[0].name").value("airForce"))
            .andDo(print())
            .andDo(document("find-all-products",
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type")
                ),
                responseFields(
                    fieldWithPath("size").description("count of products").type(JsonFieldType.NUMBER),
                    fieldWithPath("productList[].id").description("id of product").type(JsonFieldType.NUMBER),
                    fieldWithPath("productList[].brand.id").description("id of brand").type(JsonFieldType.NUMBER),
                    fieldWithPath("productList[].brand.name").description("name of brand").type(JsonFieldType.STRING),
                    fieldWithPath("productList[].name").description("name of product").type(JsonFieldType.STRING),
                    fieldWithPath("productList[].productInfo.modelNumber").description("modelNumber of product")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("productList[].productInfo.releaseDate").description("release date of product")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("productList[].productInfo.color").description("color of product")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("productList[].productInfo.releasePrice").description("release price of product")
                        .type(JsonFieldType.NUMBER)
                )
            ));

        verify(productService).findAll();
    }

    @Test
    @DisplayName("상품을 저장할 수 있다.")
    void saveProductTest() throws Exception {
        //given
        BrandResponse nikeResponse = new BrandResponse(1L, "NIKE");
        ProductInfo nikeInfo = new ProductInfo("NIKE_1", LocalDateTime.now(), "RED", 10000L);
        ProductSaveRequest productSaveRequest = new ProductSaveRequest(1L, "airForce", nikeInfo, 250);

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
            .andDo(print())
            .andDo(document("save-product",
                requestHeaders(
                    headerWithName(CONTENT_TYPE).description("content type")
                ),
                requestFields(
                    fieldWithPath("brandId").description("id of brand").type(JsonFieldType.NUMBER),
                    fieldWithPath("name").description("name of product").type(JsonFieldType.STRING),
                    fieldWithPath("productInfo.modelNumber").description("model number of product")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("productInfo.releaseDate").description("release date of product")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("productInfo.color").description("color of product").type(JsonFieldType.STRING),
                    fieldWithPath("productInfo.releasePrice").description("release price of product").type(JsonFieldType.NUMBER),
                    fieldWithPath("size").description("size of product").type(JsonFieldType.NUMBER)
                ),
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type")
                ),
                responseFields(
                    fieldWithPath("id").description("id of product").type(JsonFieldType.NUMBER),
                    fieldWithPath("brand.id").description("id of brand").type(JsonFieldType.NUMBER),
                    fieldWithPath("brand.name").description("name of brand").type(JsonFieldType.STRING),
                    fieldWithPath("name").description("name of product").type(JsonFieldType.STRING),
                    fieldWithPath("productInfo.modelNumber").description("modelNumber of product")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("productInfo.releaseDate").description("release date of product")
                        .type(JsonFieldType.STRING),
                    fieldWithPath("productInfo.color").description("color of product").type(JsonFieldType.STRING),
                    fieldWithPath("productInfo.releasePrice").description("release price of product")
                        .type(JsonFieldType.NUMBER)
                )
            ));
    }

    @Test
    @DisplayName("id로 상품을 수정할 수 있다.")
    void updateProductTest() throws Exception {
        //given
        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            1L,
            "airForce",
            "AAA",
            "Blue");

        given(productService.update(productUpdateRequest)).willReturn(
            new ProductUpdateResponse(
                "NIKE",
                "airForce",
                "Blue",
                "AAA"
            )
        );

        String requestBody = objectMapper.writeValueAsString(productUpdateRequest);

        //when & then
        mockMvc.perform(
                patch("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brandName").value("NIKE"))
            .andExpect(jsonPath("$.name").value("airForce"))
            .andDo(print())
            .andDo(document("update-product",
                requestHeaders(
                    headerWithName(CONTENT_TYPE).description("content type")
                ),
                requestFields(
                    fieldWithPath("brandId").description("id of brand").type(JsonFieldType.NUMBER),
                    fieldWithPath("productName").description("name of product").type(JsonFieldType.STRING),
                    fieldWithPath("modelNumber").description("model number of product").type(JsonFieldType.STRING),
                    fieldWithPath("color").description("color of product").type(JsonFieldType.STRING)
                ),
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type")
                ),
                responseFields(
                    fieldWithPath("brandName").description("name of brand").type(JsonFieldType.STRING),
                    fieldWithPath("name").description("name of product").type(JsonFieldType.STRING),
                    fieldWithPath("color").description("color of product").type(JsonFieldType.STRING),
                    fieldWithPath("modelNumber").description("modelNumber of product").type(JsonFieldType.STRING)
                )
            ));
    }
}
