package com.programmers.dev.bidding.domain;

import com.programmers.dev.common.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BiddingRepository extends JpaRepository<Bidding, Long> {

    @Query("select b from Bidding b where b.status = :status")
    List<Bidding> findLiveBidding(@Param("status") Status status);
}
