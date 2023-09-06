package com.programmers.dev.kream.purchasebidding.application;

import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;
import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseBiddingNowRequest;
import com.programmers.dev.kream.sellbidding.domain.SellBidding;
import com.programmers.dev.kream.sellbidding.domain.SellBiddingRepository;
import com.programmers.dev.kream.user.domain.Address;
import com.programmers.dev.kream.user.domain.User;
import com.programmers.dev.kream.user.domain.UserRepository;
import com.programmers.dev.kream.user.domain.UserRole;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@SpringBootTest
@Transactional
class PurchaseBiddingNowTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseBiddingService purchaseBiddingService;

    @Autowired
    private SizedProductRepository sizedProductRepository;

    @Autowired
    private SellBiddingRepository sellBiddingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("구매자는 계좌잔액이 충분할 경우 판매입찰 상품에 대해 즉시구매를 할 수 있다.")
    void 계좌잔액이_충분하면_즉시구매_성공() {
        //given
        SizedProduct targetSizedProduct = targetSizedProduct();

        User purchaser = getPurchaserWithAccountAmount(5000L);
        Long purchaserTargetPrice = 5000L;

        User seller = getSeller();
        SellBidding sellerRegisterdSellBidding = getSellerRegisterdSellBidding(seller.getId(), targetSizedProduct.getId());

        //when
        PurchaseBiddingNowRequest request = new PurchaseBiddingNowRequest(purchaserTargetPrice, sellerRegisterdSellBidding.getSizedProductId());
        Long purchaseBiddingId = purchaseBiddingService.purchaseNow(purchaser.getId(), request);
        PurchaseBidding purchaseBidding = purchaseBiddingService.findById(purchaseBiddingId);

        //then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(purchaseBidding.getStatus()).isEqualTo(Status.SHIPPED);
            soft.assertThat(purchaser.getAccount()).isEqualTo(0L);
        });
    }

    @Test
    @DisplayName("구매자는 계좌잔액이 충분하지 않을 경우 판매입찰 상품에 대해 즉시구매를 할 수 없다.")
    void 계좌잔액이_부족하면_즉시구매_실패() {
        //given
        SizedProduct targetSizedProduct = targetSizedProduct();

        User purchaser = getPurchaserWithAccountAmount(4000L);
        Long purchaserTargetPrice = 5000L;

        User seller = getSeller();
        SellBidding sellerRegisterdSellBidding = getSellerRegisterdSellBidding(seller.getId(), targetSizedProduct.getId());

        //when && then
        PurchaseBiddingNowRequest request = new PurchaseBiddingNowRequest(purchaserTargetPrice, sellerRegisterdSellBidding.getSizedProductId());
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> {
                    Long purchaseBiddingId = purchaseBiddingService.purchaseNow(purchaser.getId(), request);
                });
    }

    private User getPurchaserWithAccountAmount(Long account) {
        return userRepository.save(
                new User("purchaser@email.com", "purchaser", "purchaseUser", account, new Address("00000", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private User getSeller() {
        return userRepository.save(
                new User("seller@email.com", "seller", "sellUser", 10000L, new Address("00001", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private SellBidding getSellerRegisterdSellBidding(Long sellerId, Long registerdSizedProductId) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(30);
        SellBidding sellBidding = new SellBidding(sellerId, registerdSizedProductId, 5000, Status.LIVE, startDate, dueDate);

        return sellBiddingRepository.save(sellBidding);
    }

    private SizedProduct targetSizedProduct() {
        Brand brand = brandRepository.save(new Brand("ADIDAS"));
        Product product = productRepository.save(new Product(brand, "아디다스 슈퍼스타", new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50000L)));

        return sizedProductRepository.save(new SizedProduct(product, 270));
    }
}
