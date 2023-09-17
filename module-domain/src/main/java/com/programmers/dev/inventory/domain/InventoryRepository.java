package com.programmers.dev.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("""
            select i
            from Inventory i
            where i.status = 'LIVE' and i.productId = :productId and i.price = :price and i.productQuality = :productQuality
            order by i.startDate limit 1
            """)
    Optional<Inventory> findOrderableInventory(@Param("productId") Long productId, @Param("price") Long price, @Param("productQuality")Inventory.ProductQuality productQuality);
}
