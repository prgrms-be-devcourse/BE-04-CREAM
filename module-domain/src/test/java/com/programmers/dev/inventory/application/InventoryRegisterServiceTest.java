package com.programmers.dev.inventory.application;

import com.programmers.dev.common.Status;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventory.dto.InventoryRegisterRequest;
import com.programmers.dev.inventory.dto.InventoryRegisterResponse;
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

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class InventoryRegisterServiceTest {

    @Autowired
    private InventoryRegisterService inventoryRegisterService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("사용자의 계좌잔액이 보증금(보관신청상품개수*3000)보다 많을 경우 보관판매 신청을 할 수 있다.")
    void 사용자의_잔액이_보증금보다_많다면_보관신청_성공() {
        //given
        User user = createUserHavingMoney(10_000L);
        Product product = getTargetProduct();
        InventoryRegisterRequest inventoryRegisterRequest = crateStoreRequest(product.getId(), 3L, user.getAddress());

        //when
        InventoryRegisterResponse response = inventoryRegisterService.register(user.getId(), inventoryRegisterRequest);

        for (Inventory inventory : response.inventoryIds()
                .stream()
                .map(inventoryId -> inventoryRepository.findById(inventoryId)
                        .orElseThrow())
                .toList()) {
            assertThat(inventory.getStatus()).isEqualTo(Status.OUT_WAREHOUSE);
        }

        //then
        assertThat(user.getAccount()).isEqualTo(1_000L);
    }

    @Test
    @DisplayName("사용자의 계좌잔액이 보증금보다 적을 경우 보관판매 신청을 할 수 없다.")
    void 사용자의_잔액이_보증금보다_적다면_보관신청_실패() {
        //given
        User user = createUserHavingMoney(8_900L);
        Product product = getTargetProduct();
        InventoryRegisterRequest inventoryRegisterRequest = crateStoreRequest(product.getId(), 3L, user.getAddress());

        //when && then
        assertThatThrownBy(() -> {
            inventoryRegisterService.register(user.getId(), inventoryRegisterRequest);
        })
                .isInstanceOf(CreamException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_ACCOUNT_MONEY.getDescription());
    }

    private User createUserHavingMoney(Long account) {
        return userRepository.save(
                new User("aaa@email.com", "aaa", "sellUser", account, new Address("00001", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private Product getTargetProduct() {
        Brand brand = new Brand("ADIDAS");
        brandRepository.save(brand);

        ProductInfo productInfo = new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50_000L);
        Product product = new Product(brand, "SUPER-STAR", productInfo, 250);

        return productRepository.save(product);
    }

    private InventoryRegisterRequest crateStoreRequest(Long productId, Long quantity, Address address) {
        return new InventoryRegisterRequest(productId, quantity, address.getZipcode(), address.getAddress(), address.getAddressDetail());
    }
}
