package com.programmers.dev.bidding.domain;

import com.programmers.dev.common.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BiddingRepository extends JpaRepository<Bidding, Long> {

    @Query("select b from Bidding b where b.status = :status")
    List<Bidding> findLiveBidding(@Param("status") Status status);

    @Query("select b from Bidding b where b.productId = :productId and b.status = :status and b.biddingType = :biddingType order by b.price desc LIMIT 1")
    Optional<Bidding> findSellBidding(@Param("productId") Long productId, @Param("status") Status status, @Param("biddingType") Bidding.BiddingType biddingType);

}
