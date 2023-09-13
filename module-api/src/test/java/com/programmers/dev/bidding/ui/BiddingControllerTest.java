package com.programmers.dev.bidding.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.bidding.domain.Bidding;
import com.programmers.dev.bidding.domain.BiddingRepository;
import com.programmers.dev.bidding.dto.BiddingResponse;
import com.programmers.dev.bidding.dto.RegisterPurchaseBiddingRequest;
import com.programmers.dev.product.domain.*;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
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
    PasswordEncoder passwordEncoder;

    @Value("${jwt.secret-key}")
    String key;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
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
        User user = saveUser();
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        RegisterPurchaseBiddingRequest request = RegisterPurchaseBiddingRequest.of(product.getId(), 100000, 20L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                        post("/api/bidding/purchase")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                                .param("userId", user.getId().toString())
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

    private User saveUser() {
        User user = new User("user@naver.com", passwordEncoder.encode("password"), "USER1", 100000L, new Address("12345", "ilsan", "seo-gu"), UserRole.ROLE_USER);
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
}
