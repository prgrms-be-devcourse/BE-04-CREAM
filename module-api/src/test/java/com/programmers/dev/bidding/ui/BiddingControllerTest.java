package com.programmers.dev.bidding.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.bidding.application.BiddingService;
import com.programmers.dev.bidding.domain.Bidding;
import com.programmers.dev.bidding.domain.BiddingRepository;
import com.programmers.dev.bidding.dto.BiddingResponse;
import com.programmers.dev.bidding.dto.RegisterBiddingRequest;
import com.programmers.dev.bidding.dto.TransactBiddingRequest;
import com.programmers.dev.common.Status;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class BiddingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BiddingRepository biddingRepository;

    @Autowired
    BiddingService biddingService;

    @Autowired
    private JwtConfigure jwtConfigure;

    @Value("${jwt.secret-key}")
    String key;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentationContextProvider) {
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
    @DisplayName("구매자가 입찰 등록 시 정상 등록 되어야 한다.")
    void registerPurchaseBidding() throws Exception {
        // given
        User user = saveUser("user@naver.com", "USER1");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        RegisterBiddingRequest request = RegisterBiddingRequest.of(product.getId(), 100000, 20L);

        String accessToken = getAccessToken(user.getId(), user.getUserRole());
        // when
        ResultActions resultActions = this.mockMvc.perform(
                        post("/api/bidding/purchase")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                )
                .andDo(
                        document("bidding-register-purchase",
                                requestHeaders(
                                        headerWithName(CONTENT_TYPE).description("content type"),
                                        headerWithName(CONTENT_LENGTH).description("content length")
                                ),
                                requestFields(
                                        fieldWithPath("productId").description("id of product"),
                                        fieldWithPath("price").description("price of bidding"),
                                        fieldWithPath("dueDate").description("duration of bidding")
                                ),
                                responseFields(
                                        fieldWithPath("biddingId").description("id of bidding")
                                )
                        ));
        // then
        resultActions.andExpect(status().isCreated());

        Bidding savedBidding = biddingRepository.findAll().get(0);

        assertAll(
                () -> assertThat(savedBidding.getBiddingType()).isEqualTo(Bidding.BiddingType.PURCHASE),
                () -> assertThat(savedBidding.getUserId()).isEqualTo(user.getId())
        );

    }

    @Test
    @DisplayName("구매자가 판매 입찰 상품에 대해 거래체결 시 거래가 성사되어야 한다.")
    void transactSellBidding() throws Exception {
        // given
        User seller = saveUser("user1@email.com", "user1");
        User buyer = saveUser("user2@email.com", "user2");

        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);
        Bidding sellBidding = saveSellBidding(seller, product);

        TransactBiddingRequest transactBiddingRequest = new TransactBiddingRequest(sellBidding.getId());

        String accessToken = getAccessToken(buyer.getId(), buyer.getUserRole());
        // when
        ResultActions resultActions = this.mockMvc.perform(
                        post("/api/bidding/purchase-now")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(transactBiddingRequest))
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                )
                .andDo(document("bidding-transact-purchase",
                                requestHeaders(
                                        headerWithName(CONTENT_TYPE).description("content type"),
                                        headerWithName(CONTENT_LENGTH).description("length of content")
                                ),
                                requestFields(
                                        fieldWithPath("biddingId").description("id of sell bidding")
                                ),
                                responseHeaders(
                                        headerWithName(LOCATION).description("created url"),
                                        headerWithName(CONTENT_TYPE).description("type of content")
                                ),
                                responseFields(
                                        fieldWithPath("biddingId").description("id of purchase bidding")
                                )
                        )
                );

        // then
        resultActions.andExpect(status().isCreated());

        Bidding savedSellBidding = biddingRepository.findById(sellBidding.getId()).orElseThrow();
        assertThat(savedSellBidding.getStatus()).isEqualTo(Status.IN_TRANSACTION);
    }

    @Test
    @DisplayName("구매자가 입찰 등록 시 정상 등록 되어야 한다.")
    void registerSellBidding() throws Exception{
        // given
        User user = saveUser("user@naver.com", "USER1");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        RegisterBiddingRequest request = RegisterBiddingRequest.of(product.getId(), 100000, 20L);

        String accessToken = getAccessToken(user.getId(), user.getUserRole());
        // when
        ResultActions resultActions = this.mockMvc.perform(
                        post("/api/bidding/sell")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                                .header(AUTHORIZATION, accessToken)
                )
                .andDo(document("bidding-register-sell",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("content type"),
                                headerWithName(CONTENT_LENGTH).description("content length")
                        ),
                        requestFields(
                                fieldWithPath("productId").description("id of product"),
                                fieldWithPath("price").description("price of bidding"),
                                fieldWithPath("dueDate").description("date of duration")
                        ),
                        responseHeaders(
                                headerWithName(LOCATION).description("created url"),
                                headerWithName(CONTENT_TYPE).description("content type")
                        ),
                        responseFields(
                                fieldWithPath("biddingId").description("id of created bidding")
                        )
                ));

        // then
        resultActions.andExpect(status().isCreated());

        Bidding savedBidding = biddingRepository.findAll().get(0);
        assertAll(
                () -> assertThat(savedBidding.getBiddingType()).isEqualTo(Bidding.BiddingType.SELL),
                () -> assertThat(savedBidding.getStatus()).isEqualTo(Status.LIVE)
        );

    }

    @Test
    @DisplayName("판매자가 구매 입찰 상품에 대해 거래체결 시 거래가 성사되어야 한다.")
    void transactPurchaseBidding() throws Exception {
        // given

        User seller = saveUser("user1@email.com", "user1");
        User buyer = saveUser("user2@email.com", "user2");

        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);
        Bidding purchaseBidding = savePurchaseBidding(buyer, product);

        TransactBiddingRequest transactBiddingRequest = new TransactBiddingRequest(purchaseBidding.getId());

        String accessToken = getAccessToken(seller.getId(), seller.getUserRole());
        // when
        ResultActions resultActions = this.mockMvc.perform(
                        post("/api/bidding/sell-now")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(transactBiddingRequest))
                                .header(AUTHORIZATION, accessToken)
                )
                .andDo(document("bidding-transact-purchase",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("content type"),
                                headerWithName(CONTENT_LENGTH).description("content length")
                        ),
                        requestFields(
                                fieldWithPath("biddingId").description("id of bidding")
                        ),
                        responseHeaders(
                                headerWithName(LOCATION).description("created url"),
                                headerWithName(CONTENT_TYPE).description("content type")
                        ),
                        responseFields(
                                fieldWithPath("biddingId").description("id of created bidding")
                        )
                ));

        // then
        resultActions.andExpect(status().isCreated());
        Bidding savedPurchaseBidding = biddingRepository.findById(purchaseBidding.getId()).orElseThrow();
        assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.IN_TRANSACTION);
    }

    /*
    todo : Security 변경 후 테스트 코드 재 작성 (임시 구현)
     */
    @Test
    @DisplayName("판매자의 물품에 대해 검수를 통과할 경우 판매 입찰의 상태가 변경 되어야 한다.")
    void inspectBiddingProduct() throws Exception{
        // given
        User seller = saveUser("user1@email.com", "user1");
        User buyer = saveUser("user2@email.com", "user2");

        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);
        Bidding purchaseBidding = savePurchaseBidding(buyer, product);

        BiddingResponse biddingResponse =
                biddingService.transactPurchaseBidding(seller.getId(), new TransactBiddingRequest(purchaseBidding.getId()));
        String accessToken = getAccessToken(buyer.getId(), buyer.getUserRole());
        // when
        ResultActions resultActions = this.mockMvc.perform(
                        post("/api/bidding/inspect/{biddingId}", biddingResponse.biddingId())
                                .param("result", "ok")
                                .header(AUTHORIZATION, accessToken)
                )
                .andDo(document("bidding-inspect",
                        responseFields(
                                fieldWithPath("message").description("message when process succeeded")
                        )));

        // then
        resultActions.andExpect(status().isOk());

        Bidding savedPurchaseBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();
        assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.AUTHENTICATED);
    }

    @Test
    @DisplayName("거래중인 입찰에 대해 입금을 할 경우 구매 입찰의 상태가 변경 되어야 한다.")
    void deposit() throws Exception{
        // given
        User seller = saveUser("user1@email.com", "user1");
        User buyer = saveUser("user2@email.com", "user2");

        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);
        Bidding sellBidding = saveSellBidding(seller, product);

        BiddingResponse biddingResponse = biddingService.transactSellBidding(buyer.getId(), "delivery", new TransactBiddingRequest(sellBidding.getId())); // 거래 체결
        biddingService.inspect(sellBidding.getId(), "ok");  // 검수 처리

        String accessToken = getAccessToken(buyer.getId(), buyer.getUserRole());
        // when
        ResultActions resultActions = this.mockMvc.perform(
                        post("/api/bidding/deposit/{biddingId}", biddingResponse.biddingId())
                                .header(AUTHORIZATION, accessToken)
                )
                .andDo(document("bidding-deposit",
                        responseFields(
                                fieldWithPath("message").description("message when process succeeded")
                        )));

        // then
        resultActions.andExpect(status().isOk());

        Bidding savedPurchaseBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();
        User savedBuyer = userRepository.findById(buyer.getId()).orElseThrow();

        assertAll(
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.DELIVERING),
                () -> assertThat(savedBuyer.getAccount()).isEqualTo(0L)
        );

    }

    @Test
    @DisplayName("거래중인 입찰에 대해 거래 종료 요청을 할 경우 구매, 판매 입찰의 상태가 종료되어야 한다.")
    void finish() throws Exception{
        // given
        User seller = saveUser("user1@email.com", "user1");
        User buyer = saveUser("user2@email.com", "user2");

        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);
        Bidding sellBidding = saveSellBidding(seller, product);

        BiddingResponse biddingResponse = biddingService.transactSellBidding(buyer.getId(), "delivery", new TransactBiddingRequest(sellBidding.getId())); // 거래 체결
        biddingService.inspect(sellBidding.getId(), "ok");  // 검수 처리
        biddingService.deposit(buyer.getId(), biddingResponse.biddingId());

        String accessToken = getAccessToken(buyer.getId(), buyer.getUserRole());
        // when
        ResultActions resultActions = this.mockMvc.perform(
                        post("/api/bidding/finish/{biddingId}", biddingResponse.biddingId())
                                .header(AUTHORIZATION, accessToken)
                )
                .andDo(document("bidding-finish",
                        responseFields(
                                fieldWithPath("message").description("message when process succeeded")
                        ))
                );
        // then
        resultActions.andExpect(status().isOk());

        User savedSeller = userRepository.findById(seller.getId()).orElseThrow();
        User savedBuyer = userRepository.findById(buyer.getId()).orElseThrow();
        Bidding savedSellBidding = biddingRepository.findById(sellBidding.getId()).orElseThrow();
        Bidding savedPurchaseBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();
        int point = savedSellBidding.getPoint();
        assertAll(
                () -> assertThat(savedSeller.getAccount()).isEqualTo(200000L + point),
                () -> assertThat(savedBuyer.getAccount()).isEqualTo(point),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.FINISHED),
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.FINISHED)
        );

    }

    private User saveUser(String email, String nickname) {
        User user = new User(email, "password", nickname, 100000L, new Address("12345", "ilsan", "seo-gu"), UserRole.ROLE_USER);
        return userRepository.saveAndFlush(user);
    }

    private Brand saveBrand(String brandName) {
        Brand nike = new Brand(brandName);
        return brandRepository.save(nike);
    }

    private Product saveProduct(Brand brand) {
        ProductInfo productInfo = new ProductInfo("na-12", LocalDateTime.now(), "RED", 100000L);
        Product product = new Product(brand, "air-jordan", productInfo, 255);
        return productRepository.save(product);
    }

    private Bidding saveSellBidding(User seller, Product product) {
        Bidding sellBidding = Bidding.registerSellBidding(seller.getId(), product.getId(), 100000, 20L);
        return biddingRepository.save(sellBidding);
    }

    private Bidding savePurchaseBidding(User buyer, Product product) {
        Bidding purchaseBidding = Bidding.registerPurchaseBidding(buyer.getId(), product.getId(), 100000, "delivery", 20L);
        return biddingRepository.save(purchaseBidding);
    }

    private String getAccessToken(Long userId, UserRole userRole) {
        return "Bearer " + JwtTokenUtils.generateAccessToken(String.valueOf(userId), userRole.toString(), jwtConfigure.getSecretKey(), jwtConfigure.getAccessTokenExpiryTimeMs());
    }
}
