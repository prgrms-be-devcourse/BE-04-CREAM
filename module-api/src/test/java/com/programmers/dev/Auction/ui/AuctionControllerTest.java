package com.programmers.dev.Auction.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.Auction.application.AuctionService;
import com.programmers.dev.Auction.dto.AuctionSaveRequest;
import com.programmers.dev.Auction.dto.AuctionSaveResponse;
import com.programmers.dev.Auction.dto.AuctionStatusChangeRequest;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.product.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AuctionService auctionService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .apply(documentationConfiguration(restDocumentationContextProvider)
                .operationPreprocessors()
                .withRequestDefaults(modifyUris().host("13.125.254.94"), prettyPrint())
                .withResponseDefaults(modifyUris().host("13.125.254.94"), prettyPrint()))
            .build();
    }

    @Test
    @DisplayName("경매 등록에 성공하면 겸매 ID를 반환받는다.")
    void saveAuctionTest() throws Exception {
        //given
        Product product = saveProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(product);

        //when && then
        mockMvc.perform(post("/api/auctions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auctionSaveRequest))
            )
            .andDo(print())
            .andDo(document("auction-save",
                requestHeaders(
                    headerWithName(CONTENT_TYPE).description("content type"),
                    headerWithName(CONTENT_LENGTH).description("content length")
                ),
                requestFields(
                    fieldWithPath("productId").description("id of product").type(JsonFieldType.NUMBER),
                    fieldWithPath("startPrice").description("start price of auction").type(JsonFieldType.NUMBER),
                    fieldWithPath("startTime").description("start time of auction").type(JsonFieldType.STRING),
                    fieldWithPath("endTime").description("end time of auction").type(JsonFieldType.STRING)
                ),
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type"),
                    headerWithName(CONTENT_LENGTH).description("content length")
                ),
                responseFields(
                    fieldWithPath("auctionId").description("id of auction").type(JsonFieldType.NUMBER)
                )
            ));
    }

    @Test
    @DisplayName("경매 상태를 변경하면 경매ID와 변경된 상태를 반환받는다.")
    void changeAuctionStatusTest() throws Exception {
        //given
        Product product = saveProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(product);
        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        AuctionStatusChangeRequest auctionStatusChangeRequest = new AuctionStatusChangeRequest(auctionSaveResponse.auctionId(), AuctionStatus.ONGOING);

        //when & then
        mockMvc.perform(patch("/api/auctions/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auctionStatusChangeRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.auctionStatus").value("ONGOING"))
            .andDo(print())
            .andDo(document("auction-status-change",
                requestHeaders(
                    headerWithName(CONTENT_TYPE).description("content type"),
                    headerWithName(CONTENT_LENGTH).description("content length")
                ),
                requestFields(
                    fieldWithPath("id").description("id of auction").type(JsonFieldType.NUMBER),
                    fieldWithPath("auctionStatus").description("status of auction").type(JsonFieldType.STRING)
                ),
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type"),
                    headerWithName(CONTENT_LENGTH).description("content length")
                ),
                responseFields(
                    fieldWithPath("auctionId").description("id of auction").type(JsonFieldType.NUMBER),
                    fieldWithPath("auctionStatus").description("status of auction").type(JsonFieldType.STRING)
                )
            ));
    }

    private static AuctionSaveRequest createAuctionSaveRequest(Product product) {
        return new AuctionSaveRequest(
            product.getId(),
            2000L,
            LocalDateTime.of(2023, 9, 13, 13, 30),
            LocalDateTime.of(2023, 9, 13, 15, 30));
    }

    private Product saveProduct() {
        Brand brand = new Brand("NIKE");
        brandRepository.save(brand);

        ProductInfo productInfo = new ProductInfo("NIKE_1", LocalDateTime.now(), "RED", 10000L);
        Product product = new Product(brand, "airForce", productInfo, 250);

        return productRepository.save(product);
    }
}
