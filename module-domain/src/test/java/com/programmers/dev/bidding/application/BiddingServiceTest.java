package com.programmers.dev.bidding.application;

import com.programmers.dev.bidding.domain.Bidding;
import com.programmers.dev.bidding.domain.BiddingRepository;
import com.programmers.dev.bidding.dto.BiddingResponse;
import com.programmers.dev.bidding.dto.RegisterBiddingrequest;
import com.programmers.dev.bidding.dto.TransactBiddingRequest;
import com.programmers.dev.common.Status;
import com.programmers.dev.product.domain.*;
import com.programmers.dev.user.domain.Address;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import com.programmers.dev.user.domain.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@Transactional
@SpringBootTest
class BiddingServiceTest {

    @Autowired
    EntityManager em;

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

    @Test
    @DisplayName("구매자가 입찰 등록 시 정상 등록 되어야 한다.")
    void registerPurchaseBidding() {
        // given
        User user = saveUser("user@naver.com", "USER");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        RegisterBiddingrequest request = RegisterBiddingrequest.of(product.getId(), 100000, 20L);
        // when
        BiddingResponse biddingResponse = biddingService.registerPurchaseBidding(user.getId(), request);

        // then
        Bidding savedBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();
        assertAll(
                () -> assertThat(savedBidding.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(savedBidding.getBiddingType()).isEqualTo(Bidding.BiddingType.PURCHASE),
                () -> assertThat(savedBidding.getStatus()).isEqualTo(Status.LIVE),
                () -> assertThat(savedBidding.getPrice()).isEqualTo(request.price()),
                () -> assertThat(savedBidding.getDueDate()).isEqualTo(savedBidding.getStartDate().plusDays(request.dueDate())),
                () -> assertThat(savedBidding.getTransactionDate()).isNull()
        );
    }

    @Test
    @DisplayName("구매자가 등록된 판매 입찰을 체결할 경우 체결이 정상적으로 체결 되어야 한다.")
    void transactSellBidding() {
        // given
        User seller = saveUser("user1@naver.com", "USER1");
        User buyer = saveUser("user2@naver.com", "USER2");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        Bidding sellBidding = saveSellBidding(seller, product);
        TransactBiddingRequest transactBiddingRequest = new TransactBiddingRequest(sellBidding.getId());


        // when
        BiddingResponse biddingResponse = biddingService.transactSellBidding(buyer.getId(), transactBiddingRequest);

        em.flush();
        em.clear();

        // then
        Bidding savedPurchaseBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();
        Bidding savedSellBidding = biddingRepository.findById(sellBidding.getId()).orElseThrow();

        assertAll(
                () -> assertThat(savedPurchaseBidding.getBiddingType()).isEqualTo(Bidding.BiddingType.PURCHASE),
                () -> assertThat(savedSellBidding.getBiddingType()).isEqualTo(Bidding.BiddingType.SELL),
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.IN_TRANSACTION),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.IN_TRANSACTION),
                () -> assertThat(savedPurchaseBidding.getTransactionDate()).isEqualTo(savedSellBidding.getTransactionDate()),
                () -> assertThat(savedPurchaseBidding.getPrice()).isEqualTo(savedSellBidding.getPrice())
        );

    }

    @Test
    @DisplayName("판매자가 입찰 등록 시 정상 등록 되어야 한다.")
    void registerSellBidding() {
        // given
        User user = saveUser("user@naver.com", "USER");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        RegisterBiddingrequest request = RegisterBiddingrequest.of(product.getId(), 100000, 20L);

        // when
        BiddingResponse biddingResponse = biddingService.registerSellBidding(user.getId(), request);

        // then
        Bidding savedBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();

        assertAll(
                () -> assertThat(savedBidding.getBiddingType()).isEqualTo(Bidding.BiddingType.SELL),
                () -> assertThat(savedBidding.getStatus()).isEqualTo(Status.LIVE),
                () -> assertThat(savedBidding.getPrice()).isEqualTo(request.price())
        );

    }

    @Test
    @DisplayName("판매자가 등록된 구매 입찰을 체결할 경우 체결이 정상적으로 체결 되어야 한다.")
    void transactPurchaseBidding() {
        // given
        User buyer = saveUser("user1@naver.com", "USER1");
        User seller = saveUser("user2@naver.com", "USER2");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);
        Bidding purchaseBidding = savePurchaseBidding(buyer, product);

        TransactBiddingRequest transactBiddingRequest = new TransactBiddingRequest(purchaseBidding.getId());

        // when
        BiddingResponse biddingResponse = biddingService.transactPurchaseBidding(seller.getId(), transactBiddingRequest);

        // then

        Bidding savedSellBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();
        Bidding savedPurchaseBidding = biddingRepository.findById(purchaseBidding.getId()).orElseThrow();

        assertAll(
                () -> assertThat(savedPurchaseBidding.getBiddingType()).isEqualTo(Bidding.BiddingType.PURCHASE),
                () -> assertThat(savedSellBidding.getBiddingType()).isEqualTo(Bidding.BiddingType.SELL),
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.IN_TRANSACTION),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.IN_TRANSACTION),
                () -> assertThat(savedPurchaseBidding.getTransactionDate()).isEqualTo(savedSellBidding.getTransactionDate()),
                () -> assertThat(savedPurchaseBidding.getPrice()).isEqualTo(savedSellBidding.getPrice())
        );
    }



    private User saveUser(String email, String nickname) {
        User user = new User(email, "password", nickname, 100000L, new Address("12345", "ilsan", "seo-gu"), UserRole.ROLE_USER);
        return userRepository.save(user);
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
        Bidding purchaseBidding = Bidding.registerPurchaseBidding(buyer.getId(), product.getId(), 100000, 20L);
        return biddingRepository.save(purchaseBidding);
    }
}
