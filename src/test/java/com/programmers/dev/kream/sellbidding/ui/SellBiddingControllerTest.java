package com.programmers.dev.kream.sellbidding.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBiddingRepository;
import com.programmers.dev.kream.sellbidding.domain.SellBidding;
import com.programmers.dev.kream.sellbidding.domain.SellBiddingRepository;
import com.programmers.dev.kream.user.domain.Address;
import com.programmers.dev.kream.user.domain.User;
import com.programmers.dev.kream.user.domain.UserRepository;
import com.programmers.dev.kream.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SellBiddingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    PurchaseBiddingRepository purchaseBiddingRepository;

    @Autowired
    SellBiddingRepository sellBiddingRepository;

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
    @DisplayName("판매입찰 관련하여 입찰 관련한 목록을 조회할 수 있다")
    void getProductInformation() throws Exception{
        // given
        User user = makeUser("naver.com", "tommy");
        Product product1 = makeProduct("nike", "air jordan",255);
        Product product2 = makeProduct("nike", "air jordan",260);
        Product product3 = makeProduct("nike", "air jordan",265);
        Product product4 = makeProduct("nike", "air jordan",270);
        makePurchaseBidding(user, product1);
        makePurchaseBidding(user, product2);


        // when
        ResultActions resultActions = this.mockMvc.perform(
                get("/api/sell/biddings")
                        .param("productName", product1.getName())
                        .param("brandName", product1.getBrand().getName())
        );

        // then
        resultActions.andExpect(status().isOk());

        resultActions.andDo(
                document("get-prodcut-information",
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("productName").description("name of product"),
                                fieldWithPath("biddingSelectLines[0].lived").description("boolean value of bidding product"),
                                fieldWithPath("biddingSelectLines[0].size").description("size of product"),
                                fieldWithPath("biddingSelectLines[0].sizedProductId").description("id of sizedProductId"),
                                fieldWithPath("biddingSelectLines[0].price").description("price of bidding")
                        )
                )
        );

    }

    @Test
    @DisplayName("판매입찰 등록을 할 수 있다.")
    void saveSellBidding() throws Exception {
        // given
        User user = makeUser("daum.net", "tommy");
        Product product = makeProduct("puma", "puma-v1", 260);

        SellBiddingRequest sellBiddingRequest = new SellBiddingRequest(190000, 15L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/sell/biddings")
                        .param("userId", user.getId().toString())
                        .param("productId", product.getId().toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sellBiddingRequest))
        );

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andDo(
                document("save-sell-bidding",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("content type header"),
                                headerWithName(CONTENT_LENGTH).description("length of content")
                        ),
                        requestFields(
                                fieldWithPath("price").description("price of sell bidding"),
                                fieldWithPath("dueDate").description("due Date from the time of now")
                        ),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("sellBiddingId").description("id of created sell bidding")
                        )
                )
        );

    }


    @Test
    @DisplayName("잘못된 id로 요청 시 요청 응답은 실패한다 ")
    void saveSellBidding_BadId() throws Exception {
        // given
        User user = makeUser("daum.net", "tommy");
        Product product = makeProduct("puma", "puma-v1", 260);

        SellBiddingRequest sellBiddingRequest = new SellBiddingRequest(190000, 15L);

        // when
        ResultActions invalidUserId = this.mockMvc.perform(
                post("/api/sell/biddings")
                        .param("userId", String.valueOf(user.getId() + 1L))
                        .param("productId", product.getId().toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sellBiddingRequest))
        );

        ResultActions invalidSizedProductId = this.mockMvc.perform(
                post("/api/sell/biddings")
                        .param("userId", user.getId().toString())
                        .param("productId", String.valueOf(product.getId() + 1L))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sellBiddingRequest))
        );

        // then
        invalidUserId.andExpect(status().isBadRequest());
        invalidSizedProductId.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("구매 입찰에 등록된 건에 대해서 판매를 할 수 있다.")
    void transactPurchaseBidding() throws Exception{
        // given
        User user1 = makeUser("naver.com", "tommy");
        User user2 = makeUser("google.com", "Nick");
        Product product = makeProduct("Nike", "air jordan", 255);
        PurchaseBidding purchaseBidding = makePurchaseBidding(user1, product);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/sell/biddings/transact")
                        .param("userId", String.valueOf(user2.getId()))
                        .param("purchaseBiddingId", String.valueOf(purchaseBidding.getId()))
        );

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andDo(
                document("transact-purchase-bidding",
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("sellBiddingId").description("id of created sell bidding")
                        ))
        );

        SellBidding savedSellBidding = sellBiddingRepository.findAll().get(0);
        User buyer = userRepository.findById(user1.getId()).get();
        User seller = userRepository.findById(user2.getId()).get();
        assertAll(
                () -> assertThat(savedSellBidding.getProductId()).isEqualTo(purchaseBidding.getProductId()),
                () -> assertThat(savedSellBidding.getSellBidderId()).isEqualTo(user2.getId()),
                () -> assertThat(savedSellBidding.getDueDate()).isEqualTo(purchaseBidding.getDueDate()),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.SHIPPED),
                () -> assertThat(purchaseBidding.getStatus()).isEqualTo(Status.SHIPPED),
                () -> assertThat(buyer.getAccount()).isEqualTo(80000L),
                () -> assertThat(seller.getAccount()).isEqualTo(120000L)
        );

    }

    private User makeUser(String email, String nickname) {
        User user = new User(email, "password", nickname, 100000L, new Address("12345", "경기도", "일산동구"), UserRole.ROLE_USER);
        userRepository.save(user);

        return user;
    }

    private Product makeProduct(String brandName, String productName, int size) {
        Brand nike = new Brand(brandName);
        brandRepository.save(nike);
        ProductInfo productInfo = new ProductInfo("A-1202020", LocalDateTime.now().minusDays(100), "RED", 20000L);
        Product product = new Product(nike, productName, productInfo, size);
        productRepository.save(product);

        return product;
    }


    private PurchaseBidding makePurchaseBidding(User user, Product product) {
        PurchaseBidding purchaseBidding = new PurchaseBidding(user.getId(), product.getId(), 20000L, Status.LIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(20));
        purchaseBiddingRepository.save(purchaseBidding);
        return purchaseBidding;
    }
}
