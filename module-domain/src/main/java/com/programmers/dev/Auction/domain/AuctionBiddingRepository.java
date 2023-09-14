package com.programmers.dev.Auction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionBiddingRepository extends JpaRepository<AuctionBidding, Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from AuctionBidding ab where ab.user.id = :userId and ab.auction.id = :auctionId and ab.price = :price")
    void deleteLastAuctionBidding(
        @Param("userId") Long userId,
        @Param("auctionId") Long auctionId,
        @Param("price") Long price);
}
