package com.programmers.dev.Auction.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.Auction.application.AuctionBiddingService;
import com.programmers.dev.Auction.application.AuctionService;
import com.programmers.dev.Auction.dto.*;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.product.domain.*;
import com.programmers.dev.security.jwt.JwtConfigure;
import com.programmers.dev.security.jwt.JwtTokenUtils;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuctionBiddingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtConfigure jwtConfigure;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuctionBiddingService auctionBiddingService;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .apply(documentationConfiguration(restDocumentationContextProvider)
                .operationPreprocessors()
                .withRequestDefaults(modifyUris().host("13.125.254.94"), prettyPrint())
                .withResponseDefaults(modifyUris().host("13.125.254.94"), prettyPrint()))
            .build();
    }

    @Test
    @DisplayName("경매가 진행중인 상태일 경우 사용자는 경매에 입찰을 할 수 있다.")
    void bidAuctionTest() throws Exception {
        //given
        User user = createUserHavingMoney(10_000L);
        String accessToken = getAccessToken(user.getId(), user.getUserRole());

        Product product = saveProduct();
        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(product);

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);
        AuctionStatusChangeRequest auctionStatusChangeRequest = createAuctionStatusChangeRequest(auctionSaveResponse);
        auctionService.changeAuctionStatus(auctionStatusChangeRequest);

        AuctionBidRequest auctionBidRequest = createAuctionBidRequest(auctionSaveResponse);

        //when && then
        mockMvc.perform(post("/api/auctions/bidding")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(auctionBidRequest))
            )
            .andDo(print())
            .andDo(document("auction-bid",
                requestHeaders(
                    headerWithName(CONTENT_TYPE).description("content type"),
                    headerWithName(CONTENT_LENGTH).description("content length")
                ),
                requestFields(
                    fieldWithPath("auctionId").description("id of auction"),
                    fieldWithPath("price").description("price of auction bidding")
                ),
                responseHeaders(
                    headerWithName(CONTENT_TYPE).description("content type"),
                    headerWithName(CONTENT_LENGTH).description("content length")
                ),
                responseFields(
                    fieldWithPath("auctionBiddingId").description("id of auction bidding").type(JsonFieldType.NUMBER),
                    fieldWithPath("userId").description("id of user").type(JsonFieldType.NUMBER),
                    fieldWithPath("price").description("price of auction bidding").type(JsonFieldType.NUMBER)
                )
            ));
    }

    @Test
    @DisplayName("경매 ID와 입찰한 가격을 통해 입찰을 취소할 수 있다.")
    void cancelAuctionBiddingTest() throws Exception {
        //given
        User user = createUserHavingMoney(10_000L);
        String accessToken = getAccessToken(user.getId(), user.getUserRole());

        Product product = saveProduct();
        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(product);

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);
        AuctionStatusChangeRequest auctionStatusChangeRequest = createAuctionStatusChangeRequest(auctionSaveResponse);
        auctionService.changeAuctionStatus(auctionStatusChangeRequest);

        AuctionBidRequest auctionBidRequest = createAuctionBidRequest(auctionSaveResponse);

        AuctionBidResponse auctionBidResponse = auctionBiddingService.bidAuction(user.getId(), auctionBidRequest);

        AuctionBiddingCancelRequest auctionBiddingCancelRequest = createAuctionBiddingCancelRequest(auctionBidRequest, auctionBidResponse);

        mockMvc.perform(delete("/api/auctions/bidding")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(auctionBiddingCancelRequest)))
            .andExpect(status().isNoContent())
            .andDo(print())
            .andDo(document("cancel-auction-bidding",
                requestHeaders(
                    headerWithName(CONTENT_TYPE).description("content type"),
                    headerWithName(CONTENT_LENGTH).description("content length")
                ),
                requestFields(
                    fieldWithPath("auctionId").description("id of auction"),
                    fieldWithPath("price").description("price of auction bidding")
                )
            ));
    }

    private static AuctionBiddingCancelRequest createAuctionBiddingCancelRequest(AuctionBidRequest auctionBidRequest, AuctionBidResponse auctionBidResponse) {
        return new AuctionBiddingCancelRequest(auctionBidRequest.auctionId(), auctionBidResponse.price());
    }

    private static AuctionBidRequest createAuctionBidRequest(AuctionSaveResponse auctionSaveResponse) {
        return new AuctionBidRequest(auctionSaveResponse.auctionId(), 3000L);
    }

    private static AuctionStatusChangeRequest createAuctionStatusChangeRequest(AuctionSaveResponse auctionSaveResponse) {
        return new AuctionStatusChangeRequest(auctionSaveResponse.auctionId(), AuctionStatus.ONGOING);
    }


    private String getAccessToken(Long userId, UserRole userRole) {
        return "Bearer " + JwtTokenUtils.generateAccessToken(String.valueOf(userId), userRole.toString(), jwtConfigure.getSecretKey(), jwtConfigure.getAccessTokenExpiryTimeMs());
    }

    private User createUserHavingMoney(Long account) {
        return userRepository.save(new User("test@email.com", "test", "sellUser", account, new Address("00001", "인천", "연수구"), UserRole.ROLE_USER));
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