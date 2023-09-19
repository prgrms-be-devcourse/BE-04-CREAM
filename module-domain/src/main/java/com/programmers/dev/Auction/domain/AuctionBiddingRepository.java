package com.programmers.dev.Auction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface AuctionBiddingRepository extends JpaRepository<AuctionBidding, Long> {

    @Query("select ab from AuctionBidding ab " +
        "where ab.auction.id = :auctionId " +
        "order by ab.price desc " +
        "limit 1")
    Optional<AuctionBidding> findTopBiddingPrice(@Param("auctionId") Long auctionId);


    @Query("select ab from AuctionBidding ab " +
        "where ab.user.id = :userId and ab.auction.id = :auctionId and ab.price = :price")
    Optional<AuctionBidding> findCancelBidding(
        @Param("userId") Long userId,
        @Param("auctionId") Long auctionId,
        @Param("price") Long price);

    @Modifying(clearAutomatically = true)
    @Query("delete from AuctionBidding ab where ab.user.id = :userId and ab.auction.id = :auctionId and ab.price = :price")
    void deleteLastAuctionBidding(
        @Param("userId") Long userId,
        @Param("auctionId") Long auctionId,
        @Param("price") Long price);
}
