package com.programmers.dev.inventory.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.common.Status;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventory.dto.InventoryOrderRequest;
import com.programmers.dev.product.domain.*;
import com.programmers.dev.security.jwt.JwtConfigure;
import com.programmers.dev.security.jwt.JwtTokenUtils;
import com.programmers.dev.user.domain.Address;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import com.programmers.dev.user.domain.UserRole;
import jakarta.transaction.Transactional;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

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

@Transactional
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerOrderTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtConfigure jwtConfigure;

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
    @DisplayName("보관판매 주문에 성공하면 보관판매주문 ID를 반환받는다.")
    void 보관판매에_주문에_성공하면_생성된_보관판매주문ID를_반환받는다() throws Exception {
        //given
        Long havingAccountMoney = 200_000L;
        User user = createUserHavingMoney(havingAccountMoney);
        String accessToken = getAccessToken(user.getId(), user.getUserRole());
        Product product = createProduct();
        Long hopedPrice = 100_000L;
        Inventory livedStatusInventory = createLivedStatusInventory(user.getId(), product.getId(), hopedPrice, Inventory.ProductQuality.COMPLETE, user.getAddress());

        //when && then
        InventoryOrderRequest request = new InventoryOrderRequest(hopedPrice, product.getId());
        mockMvc.perform(post("/api/inventories/order")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .param("productQuality", Inventory.ProductQuality.COMPLETE.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andDo(document("inventory-order",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("content type"),
                                headerWithName(CONTENT_LENGTH).description("content length")
                        ),
                        requestFields(
                                fieldWithPath("price").description("price of inventory"),
                                fieldWithPath("productId").description("productId of inventory")
                        ),
                        responseFields(
                                fieldWithPath("inventoryOrderId").description("id of inventoryOrder")
                        )
                ));
    }

    private String getAccessToken(Long userId, UserRole userRole) {
        return "Bearer " + JwtTokenUtils.generateAccessToken(String.valueOf(userId), userRole.toString(), jwtConfigure.getSecretKey(), jwtConfigure.getAccessTokenExpiryTimeMs());
    }

    private User createUserHavingMoney(Long account) {
        return userRepository.save(
                new User("aaa@email.com", "aaa", "sellUser", account, new Address("00001", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private Product createProduct() {
        Brand brand = new Brand("ADIDAS");
        brandRepository.save(brand);

        ProductInfo productInfo = new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50_000L);
        Product product = new Product(brand, "SUPER-STAR", productInfo, 250);

        return productRepository.save(product);
    }

    private Inventory createLivedStatusInventory(Long userId, Long productId, Long price, Inventory.ProductQuality productQuality, Address address) {
        Inventory inventory = new Inventory(userId, productId, Status.IN_WAREHOUSE, address, LocalDateTime.now());
        inventory.authenticationPassedWithProductQuality(productQuality);
        inventory.lived(price);

        return inventoryRepository.save(inventory);
    }
}
