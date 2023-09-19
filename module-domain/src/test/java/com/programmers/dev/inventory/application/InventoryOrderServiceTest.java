package com.programmers.dev.inventory.application;

import com.programmers.dev.common.Status;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventory.dto.InventoryOrderRequest;
import com.programmers.dev.inventory.dto.InventoryOrderResponse;
import com.programmers.dev.inventoryorder.domain.InventoryOrder;
import com.programmers.dev.inventoryorder.domain.InventoryOrderRepository;
import com.programmers.dev.product.domain.*;
import com.programmers.dev.settlement.application.SettlementService;
import com.programmers.dev.settlement.domain.Settlement;
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
class InventoryOrderServiceTest {

    @Autowired
    private InventoryOrderService inventoryOrderService;

    @Autowired
    private InventoryOrderRepository inventoryOrderRepository;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("구매자는 본인 계좌에 지불할 수 있는 돈이 있으면, LIVE 상태를 가지고 있는 보관판매 상품을 구입할 수 있다.")
    void 구매자_보관판매_상품_구입_성공() {
        //given
        Long hopedPrice = 100_000L;
        Long havingAccountMoney = 200_000L;
        User orderer = createUserHavingMoney(havingAccountMoney);
        Product product = createProduct();
        Inventory.ProductQuality productQuality = Inventory.ProductQuality.COMPLETE;
        Inventory inventory = createLivedStatusInventory(orderer.getId(), product.getId(), hopedPrice, productQuality, orderer.getAddress());

        //when
        InventoryOrderRequest request = new InventoryOrderRequest(hopedPrice, product.getId());
        InventoryOrderResponse response = inventoryOrderService.order(orderer.getId(), request, productQuality);

        //then
        User updatedOrderer = userRepository.findById(orderer.getId()).get();
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).get();
        InventoryOrder inventoryOrder = inventoryOrderRepository.findById(response.inventoryOrder()).get();

        assertSoftly(soft -> {
            soft.assertThat(updatedOrderer.getAccount()).isEqualTo(havingAccountMoney-hopedPrice);
            soft.assertThat(updatedInventory.getTransactionDate()).isEqualTo(inventoryOrder.getTransactionDate());
            soft.assertThat(updatedInventory.getStatus()).isEqualTo(Status.DELIVERING);
            soft.assertThat(inventoryOrder.getInventoryId()).isEqualTo(updatedInventory.getId());
            soft.assertThat(inventoryOrder.getInventoryOrderStatus()).isEqualTo(InventoryOrder.InventoryOrderStatus.DELIVERING);
        });
    }

    @Test
    @DisplayName("구매자는 본인 계좌에 지불할 수 있는 돈이 없다면, LIVE 상태를 가지고 있는 보관판매 상품을 구입할 수 없다.")
    void 구매자_보관판매_상품_구입_실패() {
        //given
        Long hopedPrice = 100_000L;
        Long havingAccountMoney = 99_000L;
        User orderer = createUserHavingMoney(havingAccountMoney);
        Product product = createProduct();
        Inventory.ProductQuality productQuality = Inventory.ProductQuality.COMPLETE;
        Inventory inventory = createLivedStatusInventory(orderer.getId(), product.getId(), hopedPrice, productQuality, orderer.getAddress());

        //when && then
        InventoryOrderRequest request = new InventoryOrderRequest(hopedPrice, product.getId());
        assertThatThrownBy(() -> {
            inventoryOrderService.order(orderer.getId(), request, productQuality);
        })
                .isInstanceOf(CreamException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_ACCOUNT_MONEY.getDescription());
    }

    @Test
    @DisplayName("판매자의 LIVE 상태를 가지고 있는 보관판매 상품이 판매될 경우, 정산 테이블에 판매된 상품의 금액만큼 환급 내역이 추가된다.")
    void 판매자_등록_보관판매_상품_판매_성공() {
        //given
        Long hopedPrice = 100_000L;
        Long havingAccountMoney = 200_000L;
        User orderer = createUserHavingMoney(havingAccountMoney);
        Product product = createProduct();
        Inventory.ProductQuality productQuality = Inventory.ProductQuality.COMPLETE;
        Inventory inventory = createLivedStatusInventory(orderer.getId(), product.getId(), hopedPrice, productQuality, orderer.getAddress());

        //when
        InventoryOrderRequest request = new InventoryOrderRequest(hopedPrice, product.getId());
        InventoryOrderResponse response = inventoryOrderService.order(orderer.getId(), request, productQuality);
        forceSettlementSaveWithOrderedCondition(inventory.getUserId(), request.price());

        //then
        Settlement settlement = settlementService.findByUserId(inventory.getUserId()).get(0);
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).get();
        InventoryOrder inventoryOrder = inventoryOrderRepository.findById(response.inventoryOrder()).get();

        assertSoftly(soft -> {
            soft.assertThat(settlement.getSettlementType()).isEqualTo(Settlement.SettlementType.DEPOSIT);
            soft.assertThat(settlement.getSettlementAmount()).isEqualTo(request.price());
            soft.assertThat(updatedInventory.getTransactionDate()).isEqualTo(inventoryOrder.getTransactionDate());
            soft.assertThat(updatedInventory.getStatus()).isEqualTo(Status.DELIVERING);
            soft.assertThat(inventoryOrder.getInventoryId()).isEqualTo(updatedInventory.getId());
            soft.assertThat(inventoryOrder.getInventoryOrderStatus()).isEqualTo(InventoryOrder.InventoryOrderStatus.DELIVERING);
        });
    }


    private User createUserHavingMoney(Long account) {
        return userRepository.save(
                new User("aaa@email.com", "aaa", "sellUser", account, new Address("00001", "인천", "연수구"), UserRole.ROLE_USER));
    }

    private Product createProduct() {
        Brand brand = new Brand("ADIDAS");
        brandRepository.save(brand);

        ProductInfo productInfo = new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50_000L);
        Product product = new Product(brand, "SUPER-STAR", productInfo, 250);

        return productRepository.save(product);
    }

    private Inventory createLivedStatusInventory(Long userId, Long productId, Long price, Inventory.ProductQuality productQuality, Address address) {
        Inventory inventory = new Inventory(userId, productId, Status.IN_WAREHOUSE, address, LocalDateTime.now());
        inventory.authenticationPassedWithProductQuality(productQuality);
        inventory.lived(price);

        return inventoryRepository.save(inventory);
    }

    //이벤트 처리로 저장되는 상황 가정
    private void forceSettlementSaveWithOrderedCondition(Long userId, Long orderedPrice) {
        settlementService.save(userId, Settlement.SettlementType.DEPOSIT, orderedPrice);
    }
}
