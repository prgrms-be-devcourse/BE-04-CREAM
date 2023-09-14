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
import java.util.NoSuchElementException;

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
        Product savedProduct = getSavedProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(savedProduct);

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        auctionService.changeAuctionStatus(new AuctionStatusChangeRequest(
            auctionSaveResponse.auctionId(),
            AuctionStatus.ONGOING));

        User user = saveUser();

        AuctionBidRequest auctionBidRequest = createAuctionBidRequest(auctionSaveResponse, 4000L);

        //when
        AuctionBidResponse auctionBidResponse = auctionBiddingService.bidAuction(user.getId(), auctionBidRequest);

        //then
        AuctionBidding auctionBidding = auctionBiddingRepository.findById(auctionBidResponse.auctionBiddingId())
            .orElseThrow(() -> new CreamException(ErrorCode.INVALID_ID));

        Assertions.assertThat(auctionBidding.getPrice()).isEqualTo(4000L);
    }

    @Test
    @DisplayName("ONGING상태가 아닌 경매에 입찰을 하면 예외가 발생한다.")
    void bidActionExceptionTest() {
        //given
        Product savedProduct = getSavedProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(savedProduct);

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        User user = saveUser();

        AuctionBidRequest auctionBidRequest = createAuctionBidRequest(auctionSaveResponse, 4000L);

        //when && then
        Assertions.assertThatThrownBy(() -> auctionBiddingService.bidAuction(user.getId(), auctionBidRequest))
            .isInstanceOf(CreamException.class);
    }


    @Test
    @DisplayName("경매 ID와 입찰한 가격을 통해서 입찰 취소를 할 수 있다.")
    void cancelAuctionBidTest() {
        //given
        Product savedProduct = getSavedProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(savedProduct);

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        auctionService.changeAuctionStatus(new AuctionStatusChangeRequest(
            auctionSaveResponse.auctionId(),
            AuctionStatus.ONGOING));

        User user = saveUser();

        AuctionBidRequest auctionBidRequest = createAuctionBidRequest(auctionSaveResponse, 4000L);

        AuctionBidResponse auctionBidResponse = auctionBiddingService.bidAuction(user.getId(), auctionBidRequest);

        AuctionBiddingCancelRequest auctionBiddingCancelRequest = new AuctionBiddingCancelRequest(auctionSaveResponse.auctionId(), 4000L);

        //when
        auctionBiddingService.cancelAuctionBid(user.getId(), auctionBiddingCancelRequest);

        //then
        Assertions.assertThatThrownBy(() -> auctionBiddingRepository.findById(auctionBidResponse.auctionBiddingId()).get())
            .isInstanceOf(NoSuchElementException.class);
    }

    private static AuctionBidRequest createAuctionBidRequest(AuctionSaveResponse auctionSaveResponse, long price) {
        return new AuctionBidRequest(auctionSaveResponse.auctionId(), price);
    }

    private User saveUser() {
        User user = new User(
            "aaa@mail.com",
            "123",
            "kkk",
            3000L,
            new Address("aaa", "bbb", "ccc"),
            UserRole.ROLE_USER);
        userRepository.save(user);
        return user;
    }

    private static AuctionSaveRequest createAuctionSaveRequest(Product savedProduct) {
        return new AuctionSaveRequest(
            savedProduct.getId(),
            1000L,
            LocalDateTime.of(2023, 9, 13, 13, 30),
            LocalDateTime.of(2023, 9, 13, 15, 30));
    }

    private Product getSavedProduct() {
        Brand nike = new Brand("NIKE");
        brandRepository.save(nike);

        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Product product = new Product(nike, "airForce", productInfo, 250);
        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }
}
