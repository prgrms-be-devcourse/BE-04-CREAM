package com.programmers.dev.bidding.application;

import com.programmers.dev.bidding.domain.Bidding;
import com.programmers.dev.bidding.domain.BiddingRepository;
import com.programmers.dev.bidding.dto.BiddingResponse;
import com.programmers.dev.bidding.dto.RegisterPurchaseBiddingRequest;
import com.programmers.dev.common.Status;
import com.programmers.dev.product.domain.*;
import com.programmers.dev.user.domain.Address;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import com.programmers.dev.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest
class BiddingServiceTest {

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
        User user = saveUser();
        Brand nike = saveBrand("nike");
        Product product = saveProduct(nike);

        RegisterPurchaseBiddingRequest request = RegisterPurchaseBiddingRequest.of(product.getId(), 100000, 20L);
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

    private User saveUser() {
        User user = new User("user@naver.com", "password", "USER", 100000L, new Address("12345", "ilsan", "seo-gu"), UserRole.ROLE_USER);
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

}
