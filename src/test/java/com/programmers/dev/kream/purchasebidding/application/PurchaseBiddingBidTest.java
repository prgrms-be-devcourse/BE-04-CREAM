package com.programmers.dev.kream.purchasebidding.application;

import com.programmers.dev.kream.common.bidding.BiddingDuration;
import com.programmers.dev.kream.product.domain.*;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;
import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseBiddingBidRequest;
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

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class PurchaseBiddingBidTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseBiddingService purchaseBiddingService;

    @Autowired
    private SizedProductRepository sizedProductRepository;

    @Test
    @DisplayName("구매자는 입찰구매 만료일을 1일 후로 설정할 수 있다.")
    void 입찰구매_1일_후_만료() {
        //given
        SizedProduct targetSizedProduct = targetSizedProduct();
        User purchaser = getPurchaser();
        PurchaseBiddingBidRequest request = new PurchaseBiddingBidRequest(50000L, 1L, BiddingDuration.DAY);

        //when
        Long savedPurchaseBiddingIdbid = purchaseBiddingService.bid(purchaser.getId(), request);
        PurchaseBidding savedPurchaseBidding = purchaseBiddingService.findById(savedPurchaseBiddingIdbid);

        //then
        LocalDateTime startDate = savedPurchaseBidding.getStartDate();
        LocalDateTime dueDate = savedPurchaseBidding.getDueDate();

        assertThat(dueDate).isEqualToIgnoringNanos(startDate.plusDays(1L));
    }

    @Test
    @DisplayName("구매자는 입찰구매 만료일을 7일 후로 설정할 수 있다.")
    void 입찰구매_7일_후_만료() {
        //given
        SizedProduct targetSizedProduct = targetSizedProduct();
        User purchaser = getPurchaser();
        PurchaseBiddingBidRequest request = new PurchaseBiddingBidRequest(50000L, 1L, BiddingDuration.WEEK);

        //when
        Long savedPurchaseBiddingIdbid = purchaseBiddingService.bid(purchaser.getId(), request);
        PurchaseBidding savedPurchaseBidding = purchaseBiddingService.findById(savedPurchaseBiddingIdbid);

        //then
        LocalDateTime startDate = savedPurchaseBidding.getStartDate();
        LocalDateTime dueDate = savedPurchaseBidding.getDueDate();

        assertThat(dueDate).isEqualToIgnoringNanos(startDate.plusDays(7L));
    }

    @Test
    @DisplayName("구매자는 입찰구매의 만료일 30일 후로 설정할 수 있다.")
    void 입찰구매_30일_후_만료() {
        //given
        SizedProduct targetSizedProduct = targetSizedProduct();
        User purchaser = getPurchaser();
        PurchaseBiddingBidRequest request = new PurchaseBiddingBidRequest(50000L, 1L, BiddingDuration.MONTH);

        //when
        Long savedPurchaseBiddingIdbid = purchaseBiddingService.bid(purchaser.getId(), request);
        PurchaseBidding savedPurchaseBidding = purchaseBiddingService.findById(savedPurchaseBiddingIdbid);

        //then
        LocalDateTime startDate = savedPurchaseBidding.getStartDate();
        LocalDateTime dueDate = savedPurchaseBidding.getDueDate();

        assertThat(dueDate).isEqualToIgnoringNanos(startDate.plusDays(30L));
    }

    private User getPurchaser() {
        return userRepository.save(
                new User("purchaser@email.com", "purchaser", "purchaseUser", 10000L, new Address("00000", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private SizedProduct targetSizedProduct() {
        Brand brand = new Brand("ADIDAS");
        ProductInfo productInfo = new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50000L);
        Product product = new Product(brand, "아디다스 슈퍼스타", productInfo);

        return sizedProductRepository.save(new SizedProduct(product, 270));
    }
}
