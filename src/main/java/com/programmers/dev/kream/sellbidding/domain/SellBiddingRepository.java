package com.programmers.dev.kream.sellbidding.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellBiddingRepository extends JpaRepository<SellBidding, Long> {
    List<SellBidding> findBySizedProductId(Long id);
}
