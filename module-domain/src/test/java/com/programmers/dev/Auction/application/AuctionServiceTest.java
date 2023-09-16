package com.programmers.dev.Auction.application;

import com.programmers.dev.Auction.domain.Auction;
import com.programmers.dev.Auction.domain.AuctionRepository;
import com.programmers.dev.Auction.dto.*;
import com.programmers.dev.common.AuctionStatus;
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

@SpringBootTest
@Transactional
class AuctionServiceTest {

    @Autowired
    AuctionService auctionService;

    @Autowired
    AuctionBiddingService auctionBiddingService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("경매 등록을 할 수 있다.")
    void auctionSaveTest() {
        //given
        Product savedProduct = saveProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(savedProduct);

        //when
        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        //then
        Auction auction = auctionRepository.findById(auctionSaveResponse.auctionId()).get();

        assertAll(
            () -> assertThat(auction.getId()).isNotNull(),
            () -> assertThat(auction.getPrice()).isNull()
        );
    }

    @Test
    @DisplayName("경매 시작 시 상태를 ONGOING으로 변경할 수 있다.")
    void startAuctionTest() {
        //given
        Product savedProduct = saveProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(savedProduct);

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        AuctionStatusChangeRequest auctionStatusChangeRequest = createAuctionStatusChangeRequest(auctionSaveResponse, AuctionStatus.ONGOING);

        //when
        auctionService.changeAuctionStatus(auctionStatusChangeRequest);

        //then
        em.flush();
        em.clear();

        Auction auction = auctionRepository.findById(auctionSaveResponse.auctionId()).get();

        assertThat(auction.getAuctionStatus()).isEqualTo(AuctionStatus.ONGOING);
    }

    @Test
    @DisplayName("경매 마감 시 상태를 FINISHED으로 변경할 수 있다.")
    void finishAuctionTest() {
        //given
        Product savedProduct = saveProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(savedProduct);

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        AuctionStatusChangeRequest statusChangeOngoingRequest = createAuctionStatusChangeRequest(auctionSaveResponse, AuctionStatus.ONGOING);

        auctionService.changeAuctionStatus(statusChangeOngoingRequest);

        User user = saveUser();

        bidAuction(auctionSaveResponse, user);

        AuctionStatusChangeRequest statusChangeFinishedRequest = createAuctionStatusChangeRequest(auctionSaveResponse, AuctionStatus.FINISHED);

        //when
        auctionService.changeAuctionStatus(statusChangeFinishedRequest);

        //then
        em.flush();
        em.clear();

        Auction auction = auctionRepository.findById(auctionSaveResponse.auctionId()).get();

        assertThat(auction.getAuctionStatus()).isEqualTo(AuctionStatus.FINISHED);
    }

    @Test
    @DisplayName("경매가 종료되었을 경우 낙찰자를 조회할 수 있다.")
    void findSuccessfulBidderTest() {
        //given
        Product savedProduct = saveProduct();

        AuctionSaveRequest auctionSaveRequest = createAuctionSaveRequest(savedProduct);

        AuctionSaveResponse auctionSaveResponse = auctionService.save(auctionSaveRequest);

        AuctionStatusChangeRequest auctionStatusChangeRequest1 = createAuctionStatusChangeRequest(auctionSaveResponse, AuctionStatus.ONGOING);

        auctionService.changeAuctionStatus(auctionStatusChangeRequest1);

        User user = saveUser();

        bidAuction(auctionSaveResponse, user);

        AuctionStatusChangeRequest auctionStatusChangeRequest = createAuctionStatusChangeRequest(auctionSaveResponse, AuctionStatus.FINISHED);

        auctionService.changeAuctionStatus(auctionStatusChangeRequest);

        SuccessfulBidderGetRequest successfulBidderGetRequest = new SuccessfulBidderGetRequest(auctionStatusChangeRequest.id());

        //when
        SuccessfulBidderGetResponse successfulBidder = auctionService.findSuccessfulBidder(successfulBidderGetRequest);

        //then
        assertThat(successfulBidder.price()).isEqualTo(4000);

    }

    private void bidAuction(AuctionSaveResponse auctionSaveResponse, User user) {
        AuctionBidRequest auctionBidRequest = createAuctionBidRequest(auctionSaveResponse, 4000L);

        auctionBiddingService.bidAuction(user.getId(), auctionBidRequest);
    }

    private AuctionStatusChangeRequest createAuctionStatusChangeRequest(AuctionSaveResponse auctionSaveResponse, AuctionStatus auctionStatus) {
        return new AuctionStatusChangeRequest(
            auctionSaveResponse.auctionId(),
            auctionStatus);
    }

    private AuctionSaveRequest createAuctionSaveRequest(Product savedProduct) {
        return new AuctionSaveRequest(
            savedProduct.getId(),
            1000L,
            LocalDateTime.of(2023, 9, 13, 13, 30),
            LocalDateTime.of(2023, 9, 13, 15, 30));
    }

    private Product saveProduct() {
        Brand nike = new Brand("NIKE");
        brandRepository.save(nike);

        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Product product = new Product(nike, "airForce", productInfo, 250);
        return productRepository.save(product);
    }

    private AuctionBidRequest createAuctionBidRequest(AuctionSaveResponse auctionSaveResponse, long price) {
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
}
