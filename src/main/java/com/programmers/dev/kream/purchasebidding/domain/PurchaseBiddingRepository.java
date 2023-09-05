package com.programmers.dev.kream.purchasebidding.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseBiddingRepository extends JpaRepository<PurchaseBidding, Long> {
    List<PurchaseBidding> findBySizedProductId(Long id);
}
