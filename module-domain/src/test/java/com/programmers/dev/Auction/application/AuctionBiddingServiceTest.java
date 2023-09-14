package com.programmers.dev.Auction.application;

import com.programmers.dev.Auction.domain.AuctionBidding;
import com.programmers.dev.Auction.domain.AuctionBiddingRepository;
import com.programmers.dev.Auction.dto.*;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.product.domain.*;
import com.programmers.dev.user.domain.Address;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import com.programmers.dev.user.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
class AuctionBiddingServiceTest {

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuctionBiddingRepository auctionBiddingRepository;

    @Autowired
    AuctionService auctionService;

    @Autowired
    AuctionBiddingService auctionBiddingService;

    @Test
    @DisplayName("ONGING상태의 경매에 입찰을 할 수 있다.")
    void bidAuctionTest() {
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

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        auctionService.changeAuctionStatus(new AuctionStatusChangeRequest(
            auctionSaveResponse.auctionId(),
            AuctionStatus.ONGOING));

        User user = new User(
            "aaa@mail.com",
            "123",
            "kkk",
            3000L,
            new Address("aaa", "bbb", "ccc"),
            UserRole.ROLE_USER);
        userRepository.save(user);

        AuctionBidRequest auctionBidRequest = new AuctionBidRequest(user.getId(), auctionSaveResponse.auctionId(), 4000L);

        //when
        AuctionBidResponse auctionBidResponse = auctionBiddingService.bidAuction(auctionBidRequest);

        //then
        AuctionBidding auctionBidding = auctionBiddingRepository.findById(auctionBidResponse.auctionBiddingId())
            .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));

        Assertions.assertThat(auctionBidding.getPrice()).isEqualTo(4000L);
    }

    @Test
    @DisplayName("ONGING상태가 아닌 경매에 입찰을 하면 예외가 발생한다.")
    void bidActionExceptionTest() {
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

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        User user = new User(
            "aaa@mail.com",
            "123",
            "kkk",
            3000L,
            new Address("aaa", "bbb", "ccc"),
            UserRole.ROLE_USER);
        userRepository.save(user);

        AuctionBidRequest auctionBidRequest = new AuctionBidRequest(user.getId(), auctionSaveResponse.auctionId(), 4000L);

        //when && then
        Assertions.assertThatThrownBy(() -> auctionBiddingService.bidAuction(auctionBidRequest))
            .isInstanceOf(CreamException.class);
    }
}