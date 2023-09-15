package com.programmers.dev.inventory.application;

import com.programmers.dev.common.Status;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventory.dto.statechange.InventoryArrivedRequest;
import com.programmers.dev.product.domain.*;
import com.programmers.dev.user.domain.Address;
import com.programmers.dev.user.domain.User;
import com.programmers.dev.user.domain.UserRepository;
import com.programmers.dev.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class InventoryArrivedTest {

    @Autowired
    private InventoryStateChangeService inventoryStateChangeService;

    @Autowired
    private InventoryFindService inventoryFindService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("발송한 보관판매 상품이 입고대기 상태일 경우 창고에 도착할 경우 입고됨 상태로 변경된다.")
    void 창고에_도착하면_입고됨_상태로_변경() {
        //given
        User user = createUser();
        Product product = createProduct();
        Inventory inventory = createInventory(user.getId(), product.getId(), user.getAddress());

        //when
        InventoryArrivedRequest request = new InventoryArrivedRequest(List.of(inventory.getId()));
        inventoryStateChangeService.warehouseArrived(request);

        //then
        Inventory updatedInventory = inventoryFindService.findById(inventory.getId());
        assertThat(updatedInventory.getStatus()).isEqualTo(Status.IN_WAREHOUSE);
    }

    private User createUser() {
        return userRepository.save(
                new User("aaa@email.com", "aaa", "sellUser", 100000L, new Address("00001", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private Product createProduct() {
        Brand brand = new Brand("ADIDAS");
        brandRepository.save(brand);

        ProductInfo productInfo = new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50_000L);
        Product product = new Product(brand, "SUPER-STAR", productInfo, 250);

        return productRepository.save(product);
    }

    private Inventory createInventory(Long userId, Long productId, Address address) {
        Inventory inventory = new Inventory(userId, productId, Inventory.InventoryType.SELL, Status.OUT_WAREHOUSE, address, LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }
}
