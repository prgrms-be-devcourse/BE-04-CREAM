package com.programmers.dev.bidding.application;

import com.programmers.dev.bidding.domain.Bidding;
import com.programmers.dev.bidding.domain.BiddingRepository;
import com.programmers.dev.bidding.dto.BiddingResponse;
import com.programmers.dev.bidding.dto.RegisterBiddingRequest;
import com.programmers.dev.bidding.dto.TransactBiddingRequest;
import com.programmers.dev.common.Status;
import com.programmers.dev.exception.CreamException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        RegisterBiddingRequest request = RegisterBiddingRequest.of(product.getId(), 100000, 20L);
        // when
        BiddingResponse biddingResponse = biddingService.registerPurchaseBidding(user.getId(), "delivery", request);

        // then
        Bidding savedBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();
        assertAll(
                () -> assertThat(savedBidding.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(savedBidding.getBiddingType()).isEqualTo(Bidding.BiddingType.PURCHASE),
                () -> assertThat(savedBidding.getStatus()).isEqualTo(Status.LIVE),
                () -> assertThat(savedBidding.getDeliveryType()).isEqualTo(Bidding.DeliveryType.DELIVERY),
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

        Bidding sellBidding = saveSellBidding(seller, product, 20L);
        TransactBiddingRequest transactBiddingRequest = new TransactBiddingRequest(sellBidding.getId());


        // when
        BiddingResponse biddingResponse = biddingService.transactSellBidding(buyer.getId(), "delivery", transactBiddingRequest);

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

        RegisterBiddingRequest request = RegisterBiddingRequest.of(product.getId(), 100000, 20L);

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
        Bidding purchaseBidding = savePurchaseBidding(buyer, product, 20L);

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

    @Test
    @DisplayName("거래 체결을 하려고 할 때 dueDate가 지났으면 체결이 되지 않아야 한다. ")
    void dueDateTest() throws InterruptedException {
        // given
        User buyer = saveUser("user1@naver.com", "USER1");
        User seller = saveUser("user2@naver.com", "USER2");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);
        Bidding purchaseBidding = savePurchaseBidding(buyer, product, 0L);
        Bidding sellBidding = saveSellBidding(seller, product, 0L);

        TransactBiddingRequest purchaseBiddingRequest = new TransactBiddingRequest(purchaseBidding.getId());
        TransactBiddingRequest sellBiddingRequest = new TransactBiddingRequest(sellBidding.getId());

        // when && then
        Thread.sleep(10);

        assertThatThrownBy(
                () -> biddingService.transactPurchaseBidding(seller.getId(), purchaseBiddingRequest)
        ).isInstanceOf(CreamException.class)
        ;
        assertThatThrownBy(
                () -> biddingService.transactSellBidding(buyer.getId(), "delivery", sellBiddingRequest)
        ).isInstanceOf(CreamException.class);

    }

    @Test
    @DisplayName("구매자가 판매 입찰에 등록된 금액보다 높은 금액으로 구매하려고 한다면 입찰 등록에 실패해야 한다.")
    void checkRequestPriceOverBiddingPrice_PURCHASE() {
        // given
        User buyer = saveUser("user1@naver.com", "USER1");
        User seller = saveUser("user2@naver.com", "USER2");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        saveSellBidding(seller, product, 10L);
        RegisterBiddingRequest registerBiddingRequest = RegisterBiddingRequest.of(product.getId(), 120000, 10L);

        // when  && then
        assertThatThrownBy(
                () ->biddingService.registerPurchaseBidding(buyer.getId(), "delivery", registerBiddingRequest)
        ).isInstanceOf(CreamException.class);

    }

    @Test
    @DisplayName("구매자가 판매 입찰에 등록된 금액보다 낮은 금액으로 구매하려고 한다면 입찰 등록에 실패해야 한다.")
    void checkRequestPriceOverBiddingPrice_SELL() {
        // given
        User buyer = saveUser("user1@naver.com", "USER1");
        User seller = saveUser("user2@naver.com", "USER2");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        savePurchaseBidding(buyer, product, 10L);
        RegisterBiddingRequest registerBiddingRequest = RegisterBiddingRequest.of(product.getId(), 80000, 10L);

        // when  && then
        assertThatThrownBy(
                () ->biddingService.registerSellBidding(seller.getId(), registerBiddingRequest)
        ).isInstanceOf(CreamException.class);

    }

    @Test
    @DisplayName("판매 상품에 대해 검수 실패시 판매 입찰은 검수 실패, 구매 입찰은 취소 되어야 한다.")
    void inspect_fail() {
        // given
        User buyer = saveUser("buyer@naver.com", "buyer");
        User seller = saveUser("seller@naver.com", "seller");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        Bidding purchaseBidding = savePurchaseBidding(buyer, product, 10L);

        BiddingResponse biddingResponse =
                biddingService.transactPurchaseBidding(seller.getId(), new TransactBiddingRequest(purchaseBidding.getId()));

        // when
        biddingService.inspect(biddingResponse.biddingId(), "fail");

        // then
        Bidding savedPurchaseBidding = biddingRepository.findById(purchaseBidding.getId()).orElseThrow();
        Bidding savedSellBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();

        assertAll(
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.CANCELLED),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.AUTHENTICATED_FAILED)
        );
    }

    @Test
    @DisplayName("판매 상품에 대해 검수 성공시 판매 입찰은 검수 완료, 구매 입찰은 거래중 이어야 한다.")
    void inspect_ok() {
        // given
        User buyer = saveUser("buyer@naver.com", "buyer");
        User seller = saveUser("seller@naver.com", "seller");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        Bidding purchaseBidding = savePurchaseBidding(buyer, product, 10L);

        BiddingResponse biddingResponse =
                biddingService.transactPurchaseBidding(seller.getId(), new TransactBiddingRequest(purchaseBidding.getId()));

        // when
        biddingService.inspect(biddingResponse.biddingId(), "ok");

        // then
        Bidding savedPurchaseBidding = biddingRepository.findById(purchaseBidding.getId()).orElseThrow();
        Bidding savedSellBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();

        assertAll(
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.IN_TRANSACTION),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.AUTHENTICATED)
        );
    }

    @Test
    @DisplayName("구매자가 입금을 할 경우 판매 입찰의 상태는 배송중, 구매입찰의 상태는 입금완료 이어야 한다.")
    void deposit() {
        // given
        User buyer = saveUser("buyer@naver.com", "buyer");
        User seller = saveUser("seller@naver.com", "seller");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        Bidding sellBidding = saveSellBidding(seller, product, 10L);

        TransactBiddingRequest transactBiddingRequest = new TransactBiddingRequest(sellBidding.getId());
        BiddingResponse biddingResponse = biddingService.transactSellBidding(buyer.getId(), "delivery", transactBiddingRequest);
        biddingService.inspect(sellBidding.getId(), "ok");

        em.flush();
        em.clear();
        // when
        biddingService.sendMoneyForBidding(buyer.getId(), biddingResponse.biddingId());

        // then
        User savedBuyer = userRepository.findById(buyer.getId()).orElseThrow();
        Bidding savedSellBidding = biddingRepository.findById(sellBidding.getId()).orElseThrow();
        Bidding savedPurchaseBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();

        assertAll(
                () -> assertThat(savedBuyer.getAccount()).isZero(),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.AUTHENTICATED),
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.DELIVERING)
        );
    }

    @Test
    @DisplayName("구매자가 거래 종료를 할 경우 모든 입찰은 거래 종료 상태로 바뀌며, 판매자의 계좌에 돈이 송금된다.")
    void finish() {
        // given
        User buyer = saveUser("buyer@naver.com", "buyer");
        User seller = saveUser("seller@naver.com", "seller");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        Bidding sellBidding = saveSellBidding(seller, product, 10L);

        TransactBiddingRequest transactBiddingRequest = new TransactBiddingRequest(sellBidding.getId());
        BiddingResponse biddingResponse = biddingService.transactSellBidding(buyer.getId(), "delivery", transactBiddingRequest);
        biddingService.inspect(sellBidding.getId(), "ok");
        biddingService.sendMoneyForBidding(buyer.getId(), biddingResponse.biddingId());

        // when
        biddingService.finish(buyer.getId(), biddingResponse.biddingId());

        // then
        User savedBuyer = userRepository.findById(buyer.getId()).orElseThrow();
        User savedSeller = userRepository.findById(seller.getId()).orElseThrow();
        Bidding savedPurchaseBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();
        Bidding savedSellBidding = biddingRepository.findById(sellBidding.getId()).orElseThrow();

        int point = savedSellBidding.getPoint();
        assertAll(
                () -> assertThat(savedBuyer.getAccount()).isEqualTo((long) point),
                () -> assertThat(savedSeller.getAccount()).isEqualTo(200000L + point),
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.DELIVERED),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.FINISHED)
        );

    }

    @Test
    @DisplayName("구매자 혹은 판매자가 취소할 때 LIVE 인 경우 취소 가능하며, 별다른 페널티는 존재하지 않는다.")
    void cancel_LIVE() {
        // given
        User seller = saveUser("seller@naver.com", "seller");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        Bidding sellBidding = saveSellBidding(seller, product, 10L);


        // when
        biddingService.cancel(seller.getId(), sellBidding.getId());

        // then
        User savedSeller = userRepository.findById(seller.getId()).orElseThrow();
        Bidding savedSellBidding = biddingRepository.findById(sellBidding.getId()).orElseThrow();

        assertAll(
                () -> assertThat(savedSeller.getAccount()).isEqualTo(100000L),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.CANCELLED)
        );
    }

    @Test
    @DisplayName("두가지의 입찰 상태가 IN_TRANSACTION인 경우에는 취소가 가능하며 일부의 페널티가 존재한다. ")
    void cancel_IN_TRANSACTION() {
        // given
        User buyer = saveUser("buyer@naver.com", "buyer");
        User seller = saveUser("seller@naver.com", "seller");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        Bidding sellBidding = saveSellBidding(seller, product, 10L);
        BiddingResponse biddingResponse =
                biddingService.transactSellBidding(buyer.getId(), "delivery", new TransactBiddingRequest(sellBidding.getId()));

        // when
        biddingService.cancel(seller.getId(), sellBidding.getId());

        // then
        User savedBuyer = userRepository.findById(buyer.getId()).orElseThrow();
        User savedSeller = userRepository.findById(seller.getId()).orElseThrow();
        Bidding savedSellBidding = biddingRepository.findById(sellBidding.getId()).orElseThrow();
        Bidding savedPurchaseBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();

        int penalty = savedSellBidding.getPrice() / 100;
        assertAll(
                () -> assertThat(savedSeller.getAccount()).isEqualTo(100000L - (long) penalty),
                () -> assertThat(savedBuyer.getAccount()).isEqualTo(100000L + (long) penalty),
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.CANCELLED),
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.CANCELLED)
        );
    }

    @Test
    @DisplayName("판매 입찰의 상품이 검수 되었을 경우 거래를 취소할 수 없다.")
    void cancel_AUTHENTICATED() {
        // given
        User buyer = saveUser("buyer@naver.com", "buyer");
        User seller = saveUser("seller@naver.com", "seller");
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        Bidding sellBidding = saveSellBidding(seller, product, 10L);
        BiddingResponse biddingResponse =
                biddingService.transactSellBidding(buyer.getId(), "delivery", new TransactBiddingRequest(sellBidding.getId()));
        biddingService.inspect(sellBidding.getId(), "ok");

        // when
        assertThatThrownBy(
                () -> biddingService.cancel(seller.getId(), sellBidding.getId())
        ).isInstanceOf(CreamException.class);

        // then
        Bidding savedSellBidding = biddingRepository.findById(sellBidding.getId()).orElseThrow();
        Bidding savedPurchaseBidding = biddingRepository.findById(biddingResponse.biddingId()).orElseThrow();

        assertAll(
                () -> assertThat(savedSellBidding.getStatus()).isEqualTo(Status.AUTHENTICATED),
                () -> assertThat(savedPurchaseBidding.getStatus()).isEqualTo(Status.IN_TRANSACTION)
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

    private Bidding saveSellBidding(User seller, Product product, long dueDate) {
        Bidding sellBidding = Bidding.registerSellBidding(seller.getId(), product.getId(), 100000, dueDate);
        return biddingRepository.save(sellBidding);
    }

    private Bidding savePurchaseBidding(User buyer, Product product, long dueDate) {
        Bidding purchaseBidding = Bidding.registerPurchaseBidding(buyer.getId(), product.getId(), 100000, "delivery", dueDate);
        return biddingRepository.save(purchaseBidding);
    }
}
