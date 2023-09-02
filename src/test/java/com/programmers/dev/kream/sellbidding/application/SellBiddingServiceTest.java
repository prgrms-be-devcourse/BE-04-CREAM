package com.programmers.dev.kream.sellbidding.application;

import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.sellbidding.domain.SellBidding;
import com.programmers.dev.kream.sellbidding.domain.SellBiddingRepository;
import com.programmers.dev.kream.sellbidding.ui.SellBiddingRequest;
import com.programmers.dev.kream.sellbidding.ui.SellBiddingResponse;
import com.programmers.dev.kream.user.domain.Address;
import com.programmers.dev.kream.user.domain.User;
import com.programmers.dev.kream.user.domain.UserRepository;
import com.programmers.dev.kream.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


@Transactional
@SpringBootTest
class SellBiddingServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SizedProductRepository sizedProductRepository;

    @Autowired
    SellBiddingRepository sellBiddingRepository;

    @Autowired
    SellBiddingService sellBiddingService;


    @Test
    @DisplayName("saveSellBidding 메서드를 통해 판매입찰 저장을 할 수 있다")
    void saveSellBidding() {
        // given
        User user = makeUser("email.com", "조단 사랑");
        SizedProduct sizedProduct = makeSizedProduct("nike", "Air Jordan", 255);
        SellBiddingRequest sellBiddingRequest = new SellBiddingRequest(190000, 30L);

        // when
        SellBiddingResponse sellBiddingResponse = sellBiddingService.saveSellBidding(user.getId(), sizedProduct.getId(), sellBiddingRequest);

        // then
        SellBidding savedSellBidding = sellBiddingRepository.findById(sellBiddingResponse.sellBiddingId()).orElseThrow();
        assertAll(
                () -> assertThat(savedSellBidding.getSellBidderId()).isEqualTo(user.getId()),
                () -> assertThat(savedSellBidding.getDueDate()).isEqualTo(savedSellBidding.getStartDate().plusDays(30)),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.LIVE)
        );
    }

    @Test
    @DisplayName("유효하지 않은 회원 및 상품 id로 판매입찰 등록시 예외 발생")
    void saveSellBiddingInvalidSizedProductId() {
        // given
        User user = makeUser("naver.com", "tommy");
        SizedProduct sizedProduct = makeSizedProduct("adidas", "푸마", 300);
        SellBiddingRequest sellBiddingRequest = new SellBiddingRequest(190000, 30L);

        // when && then
        assertThatThrownBy(
                () -> sellBiddingService.saveSellBidding(user.getId(), sizedProduct.getId() + 1, sellBiddingRequest)
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(
                () -> sellBiddingService.saveSellBidding(user.getId() + 1, sizedProduct.getId(), sellBiddingRequest)
        ).isInstanceOf(IllegalArgumentException.class);

    }


    private User makeUser(String email, String nickname) {
        User user = new User(email, "password", nickname, 10000L, new Address("12345", "경기도", "일산동구"), UserRole.ROLE_USER);
        userRepository.save(user);

        return user;
    }

    private SizedProduct makeSizedProduct(String brandName, String productName, int size) {
        Brand nike = new Brand(brandName);
        ProductInfo productInfo = new ProductInfo("A-1202020", LocalDateTime.now().minusDays(100), "RED", 180000L);
        Product product = new Product(nike, productName, productInfo);
        SizedProduct sizedProduct = new SizedProduct(product, size);
        sizedProductRepository.save(sizedProduct);

        return sizedProduct;
    }

}
