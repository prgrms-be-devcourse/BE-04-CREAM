package com.programmers.dev.Auction.application;

import com.programmers.dev.Auction.domain.Auction;
import com.programmers.dev.Auction.domain.AuctionRepository;
import com.programmers.dev.Auction.dto.AuctionSaveRequest;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.product.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class AuctionServiceTest {

    @Autowired
    AuctionService auctionService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Test
    @DisplayName("경매 등록을 할 수 있다.")
    void auctionSaveTest() {
        //given
        Brand nike = new Brand("NIKE");
        brandRepository.save(nike);

        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Product product = new Product(nike, "airForce", productInfo, 250);
        Product savedProduct = productRepository.save(product);

        AuctionSaveRequest auctionSaveRequest = new AuctionSaveRequest(
            savedProduct.getId(),
            1000L,
            LocalDateTime.of(2023, 9, 13, 13, 30),
            LocalDateTime.of(2023, 9, 13, 15, 30));

        //when
        Long savedId = auctionService.save(auctionSaveRequest);

        //then
        Auction auction = auctionRepository.findById(savedId).get();

        assertAll(
            () -> Assertions.assertThat(savedId).isEqualTo(1),
            () -> Assertions.assertThat(auction.getPrice()).isNull()
        );
    }
}
