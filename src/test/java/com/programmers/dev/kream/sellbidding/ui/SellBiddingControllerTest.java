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
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
todo : @ControllerAdvice 구현 후 예외 처리에 대한 코드 구체화
todo : RestDocs 구현 후 API 명세화 하기
 */
@Transactional
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
    SizedProductRepository sizedProductRepository;

    @Autowired
    PurchaseBiddingRepository purchaseBiddingRepository;

    @Autowired
    SellBiddingRepository sellBiddingRepository;

    @Test
    @DisplayName("판매입찰 등록을 할 수 있다.")
    void saveSellBidding() throws Exception {
        // given
        User user = makeUser("daum.net", "tommy");
        SizedProduct sizedProduct = makeSizedProduct("puma", "puma-v1", 260);

        SellBiddingRequest sellBiddingRequest = new SellBiddingRequest(190000, 15L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/sell/biddings")
                        .param("userId", user.getId().toString())
                        .param("sizedProductId", sizedProduct.getId().toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sellBiddingRequest))
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    /*
    해당 테스트 코드의 경우 @ControllerAdvice 구현 후 상세화 할 예정
     */
    @Test
    @DisplayName("잘못된 id로 요청 시 요청 응답은 실패한다 ")
    @Disabled
    void saveSellBidding_BadId() throws Exception {
        // given
        User user = makeUser("daum.net", "tommy");
        SizedProduct sizedProduct = makeSizedProduct("puma", "puma-v1", 260);

        SellBiddingRequest sellBiddingRequest = new SellBiddingRequest(190000, 15L);

        // when
        ResultActions invalidUserId = this.mockMvc.perform(
                post("/api/sell/biddings")
                        .param("userId", String.valueOf(user.getId() + 1L))
                        .param("sizedProductId", sizedProduct.getId().toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sellBiddingRequest))
        );

        ResultActions invalidSizedProductId = this.mockMvc.perform(
                post("/api/sell/biddings")
                        .param("userId", user.getId().toString())
                        .param("sizedProductId", String.valueOf(sizedProduct.getId() + 1L))
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
        SizedProduct sizedProduct = makeSizedProduct("Nike", "air jordan", 255);
        PurchaseBidding purchaseBidding = makePurchaseBidding(user1, sizedProduct);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/sell/biddings/transact")
                        .param("userId", String.valueOf(user2.getId()))
                        .param("purchaseBiddingId", String.valueOf(purchaseBidding.getId()))
        );

        // then
        resultActions.andExpect(status().isOk());
        SellBidding savedSellBidding = sellBiddingRepository.findAll().get(0);
        assertAll(
                () -> assertThat(savedSellBidding.getSizedProductId()).isEqualTo(purchaseBidding.getSizedProductId()),
                () -> assertThat(savedSellBidding.getSellBidderId()).isEqualTo(user2.getId()),
                () -> assertThat(savedSellBidding.getDueDate()).isEqualTo(purchaseBidding.getDueDate()),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.SHIPPED),
                () -> assertThat(purchaseBidding.getStatus()).isEqualTo(Status.SHIPPED)
        );

    }

    private User makeUser(String email, String nickname) {
        User user = new User(email, "password", nickname, 10000L, new Address("12345", "경기도", "일산동구"), UserRole.ROLE_USER);
        userRepository.save(user);

        return user;
    }

    private SizedProduct makeSizedProduct(String brandName, String productName, int size) {
        Brand nike = new Brand(brandName);
        brandRepository.save(nike);
        ProductInfo productInfo = new ProductInfo("A-1202020", LocalDateTime.now().minusDays(100), "RED", 180000L);
        Product product = new Product(nike, productName, productInfo);
        productRepository.save(product);
        SizedProduct sizedProduct = new SizedProduct(product, size);
        sizedProductRepository.save(sizedProduct);

        return sizedProduct;
    }

    private PurchaseBidding makePurchaseBidding(User user1, SizedProduct sizedProduct) {
        PurchaseBidding purchaseBidding = new PurchaseBidding(user1.getId(), sizedProduct.getId(), 150000L, Status.LIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(20));
        purchaseBiddingRepository.save(purchaseBidding);
        return purchaseBidding;
    }
}
