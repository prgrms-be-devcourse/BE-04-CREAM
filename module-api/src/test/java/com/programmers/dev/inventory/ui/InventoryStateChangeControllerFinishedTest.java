package com.programmers.dev.inventory.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.common.Status;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventory.dto.statechange.InventoryFinishedRequest;
import com.programmers.dev.inventoryorder.domain.InventoryOrder;
import com.programmers.dev.inventoryorder.domain.InventoryOrderRepository;
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
class InventoryStateChangeControllerFinishedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryOrderRepository inventoryOrderRepository;

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
    @DisplayName("요청한 inventoryId 대해 거래종료 처리가 되면, inventoryId 를 반환받는다.")
    void 배송완료로_인한_거래종료() throws Exception {
        //given
        User orderer = createOrderUser();
        User seller = createSellUser();

        String accessToken = getAccessToken(orderer.getId(), orderer.getUserRole());
        Product product = createProduct();

        Long price = 10000L;
        LocalDateTime transactionTime = LocalDateTime.now();
        Inventory inventory = createDeilveringStatusInventory(orderer.getId(), product.getId(), orderer.getAddress(), price, transactionTime);
        InventoryOrder inventoryOrder = createdInventoryOrder(seller.getId(), inventory.getId(), price, transactionTime);

        //when && then
        mockMvc.perform(post("/api/inventories/state-change/{inventoryId}/finished", inventory.getId())
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                )
                .andDo(print())
                .andDo(document("inventory-finished",
                        responseFields(
                                fieldWithPath("inventoryId").description("id of inventory")
                        )
                ));
    }

    private String getAccessToken(Long userId, UserRole userRole) {
        return "Bearer " + JwtTokenUtils.generateAccessToken(String.valueOf(userId), userRole.toString(), jwtConfigure.getSecretKey(), jwtConfigure.getAccessTokenExpiryTimeMs());
    }

    private User createOrderUser() {
        return userRepository.save(new User("orderer@email.com", "test", "orderUser", 10_000L, new Address("00001", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private User createSellUser() {
        return userRepository.save(new User("seller@email.com", "test2", "sellUser", 10_000L, new Address("00001", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private Product createProduct() {
        Brand brand = new Brand("ADIDAS");
        brandRepository.save(brand);

        ProductInfo productInfo = new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50_000L);
        Product product = new Product(brand, "SUPER-STAR", productInfo, 250);

        return productRepository.save(product);
    }

    private Inventory createDeilveringStatusInventory(Long userId, Long productId, Address address, Long price, LocalDateTime transactionTime) {
        Inventory inventory = new Inventory(userId, productId, Status.IN_WAREHOUSE, address, LocalDateTime.now());
        inventory.authenticationPassedWithProductQuality(Inventory.ProductQuality.COMPLETE);
        inventory.lived(price);
        inventory.ordered(transactionTime);

        return inventoryRepository.save(inventory);
    }

    private InventoryOrder createdInventoryOrder(Long userId, Long inventoryId, Long orderedPrice, LocalDateTime transactionTime) {
        InventoryOrder inventoryOrder = new InventoryOrder(userId, inventoryId, orderedPrice, transactionTime);

        return inventoryOrderRepository.save(inventoryOrder);
    }
}
