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
    private SellBiddingRepository sellBiddingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("구매자는 계좌잔액이 충분할 경우 판매입찰 상품에 대해 즉시구매를 할 수 있다.")
    void 계좌잔액이_충분하면_즉시구매_성공() {
        //given
        Product product = targetProduct();

        User purchaser = getPurchaserWithAccountAmount(5000L);
        Long purchaserTargetPrice = 5000L;

        User seller = getSeller();
        SellBidding sellerRegisterdSellBidding = getSellerRegisterdSellBidding(seller.getId(), product.getId());

        //when
        PurchaseBiddingNowRequest request = new PurchaseBiddingNowRequest(purchaserTargetPrice, sellerRegisterdSellBidding.getProductId());
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
        Product product = targetProduct();

        User purchaser = getPurchaserWithAccountAmount(4000L);
        Long purchaserTargetPrice = 5000L;

        User seller = getSeller();
        SellBidding sellerRegisterdSellBidding = getSellerRegisterdSellBidding(seller.getId(), product.getId());

        //when && then
        PurchaseBiddingNowRequest request = new PurchaseBiddingNowRequest(purchaserTargetPrice, sellerRegisterdSellBidding.getProductId());
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

    private SellBidding getSellerRegisterdSellBidding(Long sellerId, Long productId) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime dueDate = startDate.plusDays(30);
        SellBidding sellBidding = new SellBidding(sellerId, productId, 5000, Status.LIVE, startDate, dueDate);

        return sellBiddingRepository.save(sellBidding);
    }

    private Product targetProduct() {
        Brand brand = new Brand("ADIDAS");
        brandRepository.save(brand);

        ProductInfo productInfo = new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50000L);
        Product product = new Product(brand, "SUPER-STAR", productInfo, 250);

        return productRepository.save(product);
    }
}
