package com.programmers.dev.kream.sellbidding.application;

import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBiddingRepository;
import com.programmers.dev.kream.sellbidding.domain.SellBidding;
import com.programmers.dev.kream.sellbidding.domain.SellBiddingRepository;
import com.programmers.dev.kream.sellbidding.ui.ProductInformation;
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
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SellBiddingService sellBiddingService;

    @Autowired
    PurchaseBiddingRepository purchaseBiddingRepository;

    @Test
    @DisplayName("판매입찰 페이지 조회시 상품 정보를 조회할 수 있다")
    void getProductInformation() {
        // given
        User user = makeUser("email.com", "조단 사랑");
        Product product = makeProduct("nike", "Air Jordan");
        SizedProduct sizedProduct1 = makeSizedProduct(product, 255);
        SizedProduct sizedProduct2 = makeSizedProduct(product, 260);
        SizedProduct sizedProduct3 = makeSizedProduct(product, 265);
        makePurchaseBidding(user, sizedProduct1);
        makePurchaseBidding(user, sizedProduct2);
        // when
        ProductInformation productInformation = sellBiddingService.getProductInformation(sizedProduct1.getProduct().getId());

        // then
        assertAll(
                () -> assertThat(productInformation.productName()).isEqualTo(sizedProduct1.getProduct().getName()),
                () -> assertThat(productInformation.biddingSelectLines().size()).isEqualTo(3),
                () -> assertThat(productInformation.biddingSelectLines().get(2).lived()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("판매입찰 페이지 조회시 잘못된 id로 조회할 경우 예외가 발생해야 한다")
    void getProductInformation_InvalidProductId() {
        // given
        User user = makeUser("email.com", "조단 사랑");
        Product product = makeProduct("nike", "Air Jordan");
        SizedProduct sizedProduct1 = makeSizedProduct(product, 255);
        SizedProduct sizedProduct2 = makeSizedProduct(product, 260);
        SizedProduct sizedProduct3 = makeSizedProduct(product, 265);
        makePurchaseBidding(user, sizedProduct1);
        makePurchaseBidding(user, sizedProduct2);

        // when && then
        assertThatThrownBy(
                () -> sellBiddingService.getProductInformation(sizedProduct1.getProduct().getId() + 1)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("saveSellBidding 메서드를 통해 판매입찰 저장을 할 수 있다")
    void saveSellBidding() {
        // given
        User user = makeUser("email.com", "조단 사랑");
        Product product = makeProduct("nike", "Air Jordan");
        SizedProduct sizedProduct = makeSizedProduct(product, 255);
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
        Product product = makeProduct("adidas", "푸마");
        SizedProduct sizedProduct = makeSizedProduct(product, 300);
        SellBiddingRequest sellBiddingRequest = new SellBiddingRequest(190000, 30L);

        // when && then
        assertThatThrownBy(
                () -> sellBiddingService.saveSellBidding(user.getId(), sizedProduct.getId() + 1, sellBiddingRequest)
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(
                () -> sellBiddingService.saveSellBidding(user.getId() + 1, sizedProduct.getId(), sellBiddingRequest)
        ).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("구매 입찰에 등록된 건을 판매입찰 할 수 있다")
    void transactPurchaseBidding() {
        // given
        User user1 = makeUser("naver.com", "tommy");
        User user2 = makeUser("google.com", "axis");
        Product product = makeProduct("Nike", "air jordan");
        SizedProduct sizedProduct = makeSizedProduct(product, 260);
        PurchaseBidding purchaseBidding = makePurchaseBidding(user1, sizedProduct);

        // when
        SellBiddingResponse sellBiddingResponse = sellBiddingService.transactPurchaseBidding(user2.getId(), purchaseBidding.getId());

        // then
        SellBidding findSellBidding = sellBiddingRepository.findById(sellBiddingResponse.sellBiddingId()).get();
        PurchaseBidding findPurchaseBidding = purchaseBiddingRepository.findById(purchaseBidding.getId()).get();
        assertAll(
                () -> assertThat(findSellBidding.getStatus()).isEqualTo(Status.SHIPPED),
                () -> assertThat(findPurchaseBidding.getStatus()).isEqualTo(Status.SHIPPED),
                () -> assertThat(Long.valueOf(findSellBidding.getPrice())).isEqualTo(findPurchaseBidding.getPrice()),
                () -> assertThat(findSellBidding.getDueDate()).isEqualTo(findPurchaseBidding.getDueDate())
        );

    }

    @Test
    @DisplayName("구매입찰을 올린 회원은 판매를 할 수 없다")
    void transactPurchaseBidding_InvalidPurchase() {
        // given
        User user1 = makeUser("naver.com", "tommy");
        Product product = makeProduct("Nike", "air jordan");
        SizedProduct sizedProduct = makeSizedProduct(product, 260);
        PurchaseBidding purchaseBidding = makePurchaseBidding(user1, sizedProduct);

        // when && then
        assertThatThrownBy(
                () -> sellBiddingService.transactPurchaseBidding(user1.getId(), purchaseBidding.getId())
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private User makeUser(String email, String nickname) {
        User user = new User(email, "password", nickname, 10000L, new Address("12345", "경기도", "일산동구"), UserRole.ROLE_USER);
        userRepository.save(user);

        return user;
    }

    private Product makeProduct(String brandName, String productName) {
        Brand nike = new Brand(brandName);
        brandRepository.save(nike);
        ProductInfo productInfo = new ProductInfo("A-1202020", LocalDateTime.now().minusDays(100), "RED", 180000L);
        Product product = new Product(nike, productName, productInfo);
        productRepository.save(product);

        return product;
    }


    private SizedProduct makeSizedProduct(Product product, int size) {
        SizedProduct sizedProduct = new SizedProduct(product, size);
        sizedProductRepository.save(sizedProduct);

        return sizedProduct;
    }

    private PurchaseBidding makePurchaseBidding(User user, SizedProduct sizedProduct) {
        PurchaseBidding purchaseBidding = new PurchaseBidding(user.getId(), sizedProduct.getId(), 150000L, Status.LIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(20));
        purchaseBiddingRepository.save(purchaseBidding);
        return purchaseBidding;
    }

}