package com.programmers.dev.kream.sellbidding.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SellBiddingRepository extends JpaRepository<SellBidding, Long> {

//    @Query(
//            nativeQuery = true,
//            value = "SELECT * FROM SELL_BIDDINGS SB " +
//                        "WHERE SB.PRICE = :price AND SB.SIZED_PRODUCT_ID = :sizedProductId AND SB.STATUS = 'LIVE'" +
//                        "ORDER BY SB.START_DATE " +
//                        "LIMIT 1"
//        )
//    Optional<SellBidding> findLowPriceBidding(@Param("price") Long price, @Param("sizedProductId") Long sizedProductId);

}
