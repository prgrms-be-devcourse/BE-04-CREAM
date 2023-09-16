package com.programmers.dev.inventory.application;

import com.programmers.dev.common.Status;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventory.dto.statechange.InventorySetPriceRequest;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


@SpringBootTest
@Transactional
class InventorySetPriceTest {

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
    @DisplayName("판매자가 보관판매 제품에대해 판매 희망가를 입력하면 거래 상태로 변경된다.")
    void 판매희망가를_입력하면_거래상태로_변경된다() {
        //given
        User user = createUser();
        Product product = createProduct();
        Inventory inventory = createAuthenticatedInventory(user.getId(), product.getId(), user.getAddress());
        Long hopedPrice = 15_0000L;

        //when
        InventorySetPriceRequest request = new InventorySetPriceRequest(hopedPrice);
        inventoryStateChangeService.setPrice(inventory.getId(), request);

        //then
        Inventory updatedInventory = inventoryFindService.findById(inventory.getId());
        assertSoftly(soft -> {
            soft.assertThat(updatedInventory.getStatus()).isEqualTo(Status.LIVE);
            soft.assertThat(updatedInventory.getPrice()).isEqualTo(hopedPrice);
        });
    }

    @Test
    @DisplayName("판매자가 보관판매 제품에대해 음수값의 판매 희망가를 입력하면 예외가 발생한다.")
    void 판매희망가를_음수로_입력하면_거래상태_등록에_실패한다() {
        //given
        User user = createUser();
        Product product = createProduct();
        Inventory inventory = createAuthenticatedInventory(user.getId(), product.getId(), user.getAddress());
        Long hopedPrice = -10_000L;

        //when
        InventorySetPriceRequest request = new InventorySetPriceRequest(hopedPrice);

        //when && then
        assertThatThrownBy(() -> {
            inventoryStateChangeService.setPrice(inventory.getId(), request);
        })
                .isInstanceOf(CreamException.class)
                .hasMessage(ErrorCode.BAD_BUSINESS_LOGIC.getDescription());
    }

    @Test
    @DisplayName("판매자가 보관판매 제품에대해 0원의 판매 희망가를 입력하면 예외가 발생한다.")
    void 판매희망가를_0원으로_입력하면_거래상태_등록에_실패한다() {
        //given
        User user = createUser();
        Product product = createProduct();
        Inventory inventory = createAuthenticatedInventory(user.getId(), product.getId(), user.getAddress());
        Long hopedPrice = 0L;

        //when
        InventorySetPriceRequest request = new InventorySetPriceRequest(hopedPrice);

        //when && then
        assertThatThrownBy(() -> {
            inventoryStateChangeService.setPrice(inventory.getId(), request);
        })
                .isInstanceOf(CreamException.class)
                .hasMessage(ErrorCode.BAD_BUSINESS_LOGIC.getDescription());
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

    private Inventory createAuthenticatedInventory(Long userId, Long productId, Address address) {
        Inventory inventory = new Inventory(userId, productId, Inventory.InventoryType.SELL, Status.AUTHENTICATED, address, LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }
}
