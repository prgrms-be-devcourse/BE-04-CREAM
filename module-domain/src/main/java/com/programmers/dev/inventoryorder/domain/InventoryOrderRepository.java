package com.programmers.dev.inventoryorder.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryOrderRepository extends JpaRepository<InventoryOrder, Long> {
}
