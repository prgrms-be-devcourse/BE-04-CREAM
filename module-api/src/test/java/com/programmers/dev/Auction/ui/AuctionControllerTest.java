package com.programmers.dev.Auction.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.Auction.application.AuctionBiddingService;
import com.programmers.dev.Auction.application.AuctionService;
import com.programmers.dev.Auction.dto.*;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.product.domain.*;
import com.programmers.dev.user.domain.Address;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import com.programmers.dev.user.domain.UserRole;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    AuctionBiddingService auctionBiddingService;

    @Autowired
    UserRepository userRepository;

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

        AuctionStatusChangeRequest auctionStatusChangeRequest = createAuctionStatusChangeRequest(auctionSaveResponse, AuctionStatus.ONGOING);

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

    @Test
    @DisplayName("경매가 종료되었을 경우 해당 경매의 낙찰자와 낙찰 금액을 알 수 있다.")
    void getSuccessfulBidderTest() throws Exception {
        //given
        Product product = saveProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(product);
        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        AuctionStatusChangeRequest ongoingRequest = createAuctionStatusChangeRequest(auctionSaveResponse, AuctionStatus.ONGOING);
        auctionService.changeAuctionStatus(ongoingRequest);

        User user = saveUser();

        AuctionBidRequest auctionBidRequest1 = createAuctionBidRequest(auctionSaveResponse, 4000L);
        AuctionBidRequest auctionBidRequest2 = createAuctionBidRequest(auctionSaveResponse, 5000L);

        auctionBiddingService.bidAuction(user.getId(), auctionBidRequest1);
        auctionBiddingService.bidAuction(user.getId(), auctionBidRequest2);

        AuctionStatusChangeRequest finishedRequest = createAuctionStatusChangeRequest(auctionSaveResponse, AuctionStatus.FINISHED);
        auctionService.changeAuctionStatus(finishedRequest);

        BidderDecisionRequest bidderDecisionRequest = new BidderDecisionRequest(auctionSaveResponse.auctionId(), true, 5000L);
        auctionBiddingService.decidePurchaseStatus(user.getId(), bidderDecisionRequest);

        SuccessfulBidderGetRequest successfulBidderGetRequest = new SuccessfulBidderGetRequest(finishedRequest.id());

        //when && then
        mockMvc.perform(get("/api/auctions/successful-bidder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(successfulBidderGetRequest)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("get-successful-bidder",
                requestHeaders(
                    headerWithName(CONTENT_TYPE).description("content type"),
                    headerWithName(CONTENT_LENGTH).description("content length")
                ),
                requestFields(
                    fieldWithPath("auctionId").description("id of auction").type(JsonFieldType.NUMBER)
                ),
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type"),
                    headerWithName(CONTENT_LENGTH).description("content length")
                ),
                responseFields(
                    fieldWithPath("auctionId").description("id of auction").type(JsonFieldType.NUMBER),
                    fieldWithPath("userId").description("id of successful bidder").type(JsonFieldType.NUMBER),
                    fieldWithPath("price").description("price of winning bid").type(JsonFieldType.NUMBER)
                )
            ));
    }

    private AuctionBidRequest createAuctionBidRequest(AuctionSaveResponse auctionSaveResponse, long price) {
        return new AuctionBidRequest(auctionSaveResponse.auctionId(), price);
    }

    private User saveUser() {
        User user = new User(
            "aaa@mail.com",
            "123",
            "kkk",
            10000L,
            new Address("aaa", "bbb", "ccc"),
            UserRole.ROLE_USER);
        userRepository.save(user);
        return user;
    }

    private AuctionStatusChangeRequest createAuctionStatusChangeRequest(AuctionSaveResponse auctionSaveResponse, AuctionStatus auctionStatus) {
        return new AuctionStatusChangeRequest(auctionSaveResponse.auctionId(), auctionStatus);
    }

    private AuctionSaveRequest createAuctionSaveRequest(Product product) {
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
