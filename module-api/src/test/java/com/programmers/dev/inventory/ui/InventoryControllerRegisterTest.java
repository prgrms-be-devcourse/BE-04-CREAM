package com.programmers.dev.inventory.ui;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.programmers.dev.common.Status;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;

import com.programmers.dev.inventory.dto.InventoryRegisterRequest;
import com.programmers.dev.inventory.dto.statechange.InventorySetPriceRequest;
import com.programmers.dev.product.domain.*;
import com.programmers.dev.security.jwt.*;
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
class InventoryControllerRegisterTest {

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
    @DisplayName("요청한 inventoryId 상품에 대한 판매희망가를 입력하면 inventoryId를 반환받는다.")
    void 판매희망가_입력() throws Exception {
        //given
        User user = createUser();
        String accessToken = getAccessToken(user.getId(), user.getUserRole());
        Product product = createProduct();
        Long hopedPrice = 100_000L;
        Inventory inventory = createInventory(user.getId(), product.getId(), user.getAddress());

        //when && then
        InventoryRegisterRequest request = crateRegisterRequest(product.getId(), 3L, user.getAddress());
        mockMvc.perform(post("/api/inventories/register", inventory.getId())
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andDo(document("inventory-register",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("content type"),
                                headerWithName(CONTENT_LENGTH).description("content length")
                        ),
                        requestFields(
                                fieldWithPath("productId").description("id of product"),
                                fieldWithPath("quantity").description("price of bidding"),
                                fieldWithPath("returnZipcode").description("returnZipcode of inventory"),
                                fieldWithPath("returnAddress").description("returnAddress of inventory"),
                                fieldWithPath("returnAddressDetail").description("returnAddressDetail of inventory")
                        ),
                        responseFields(
                                fieldWithPath("inventoryIds").description("created inventoryIds")
                        )
                ));
    }

    private String getAccessToken(Long userId, UserRole userRole) {
        return "Bearer " + JwtTokenUtils.generateAccessToken(String.valueOf(userId), userRole.toString(), jwtConfigure.getSecretKey(), jwtConfigure.getAccessTokenExpiryTimeMs());
    }

    private User createUser() {
        return userRepository.save(new User("test@email.com", "test", "sellUser", 10_000L, new Address("00001", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private Product createProduct() {
        Brand brand = new Brand("ADIDAS");
        brandRepository.save(brand);

        ProductInfo productInfo = new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50_000L);
        Product product = new Product(brand, "SUPER-STAR", productInfo, 250);

        return productRepository.save(product);
    }

    private Inventory createInventory(Long userId, Long productId, Address address) {
        Inventory inventory = new Inventory(userId, productId, Status.AUTHENTICATED, address, LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    private InventoryRegisterRequest crateRegisterRequest(Long productId, Long quantity, Address address) {
        return new InventoryRegisterRequest(productId, quantity, address.getZipcode(), address.getAddress(), address.getAddressDetail());
    }
}
