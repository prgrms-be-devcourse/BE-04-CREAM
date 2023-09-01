package com.programmers.dev.kream.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SizedProductRepository extends JpaRepository<SizedProduct, Long> {

    @Query("select sp from SizedProduct sp where sp.product.id = :productId")
    List<SizedProduct> findAllByProductId(@Param("productId") Long productId);

}
