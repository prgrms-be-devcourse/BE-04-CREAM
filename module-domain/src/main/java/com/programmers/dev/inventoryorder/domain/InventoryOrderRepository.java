package com.programmers.dev.inventoryorder.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryOrderRepository extends JpaRepository<InventoryOrder, Long> {

    Optional<InventoryOrder> findByInventoryId(Long inventoryId);

    List<InventoryOrder> findAllByUserId(Long userId);
}
