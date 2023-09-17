package com.programmers.dev.inventory.application;

import com.programmers.dev.common.CostCalculator;
import com.programmers.dev.common.CostType;
import com.programmers.dev.common.PenaltyType;
import com.programmers.dev.common.Status;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventory.dto.statechange.InventoryAuthenticateFailRequest;
import com.programmers.dev.inventory.dto.statechange.InventoryAuthenticatePassRequest;
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
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


@SpringBootTest
@Transactional
class InventoryAuthenticationTest {

    @Autowired
    private InventoryStateChangeService inventoryStateChangeService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private CostCalculator costCalculator;

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
    @DisplayName("상품이 100점으로 검수에 성공하면, AUTHENTICATED, COMPLETE 상태를 가지고 SETTLEMENTS 테이블에 DEPOSIT 데이터가 생성된다.")
    void 발송된_상품이_100점_검수_합격() {
        //given
        User user = createUser();
        Product product = createProduct();
        Inventory inventory = createInventory(user.getId(), product.getId(), user.getAddress());

        //when
        InventoryAuthenticatePassRequest request = new InventoryAuthenticatePassRequest(Inventory.ProductQuality.COMPLETE);
        inventoryStateChangeService.authenticatePassed(inventory.getId(), request);
        forceSettlementSaveWithAuthenticationPassedCondition(user.getId());

        //then
        Inventory updatedInventory = inventoryFindService.findById(inventory.getId());
        Settlement settlement = settlementService.findByUserId(user.getId()).get(0);

        assertSoftly(soft -> {
            soft.assertThat(updatedInventory.getStatus()).isEqualTo(Status.AUTHENTICATED);
            soft.assertThat(updatedInventory.getProductQuality()).isEqualTo(Inventory.ProductQuality.COMPLETE);
            soft.assertThat(settlement.getSettlementType()).isEqualTo(Settlement.SettlementType.DEPOSIT);
            soft.assertThat(settlement.getSettlementAmount()).isEqualTo(CostType.PROTECTION.getCost());
        });
    }

    @Test
    @DisplayName("상품이 95점으로 검수에 성공하면, AUTHENTICATED, INCOMPLETE 상태를 가지고 SETTLEMENTS 테이블에 DEPOSIT 데이터가 생성된다.")
    void 발송된_상품이_95점_검수_합격() {
        //given
        User user = createUser();
        Product product = createProduct();
        Inventory inventory = createInventory(user.getId(), product.getId(), user.getAddress());

        //when
        InventoryAuthenticatePassRequest request = new InventoryAuthenticatePassRequest(Inventory.ProductQuality.INCOMPLETE);
        inventoryStateChangeService.authenticatePassed(inventory.getId(), request);
        forceSettlementSaveWithAuthenticationPassedCondition(user.getId());

        //then
        Inventory updatedInventory = inventoryFindService.findById(inventory.getId());
        Settlement settlement = settlementService.findByUserId(user.getId()).get(0);

        assertSoftly(soft -> {
            soft.assertThat(updatedInventory.getStatus()).isEqualTo(Status.AUTHENTICATED);
            soft.assertThat(updatedInventory.getProductQuality()).isEqualTo(Inventory.ProductQuality.INCOMPLETE);
            soft.assertThat(settlement.getSettlementType()).isEqualTo(Settlement.SettlementType.DEPOSIT);
            soft.assertThat(settlement.getSettlementAmount()).isEqualTo(CostType.PROTECTION.getCost());
        });
    }

    @Test
    @DisplayName("상품이 검수에 실패하면, AUTHENTICATION_FAILED 상태를 가지고 SETTLEMENTS 테이블에 WITHDRAW 데이터가 생성된다.")
    void 발송된_상품이_검수_실패() {
        //given
        User user = createUser();
        Product product = createProduct();
        Inventory inventory = createInventory(user.getId(), product.getId(), user.getAddress());

        //when
        InventoryAuthenticateFailRequest request = new InventoryAuthenticateFailRequest(PenaltyType.PRODUCT_DAMEGED);
        inventoryStateChangeService.authenticateFailed(inventory.getId(), request);

        Long penaltyCost = costCalculator.calculatePenaltyCost(product.getProductInfo().getReleasePrice(), PenaltyType.PRODUCT_DAMEGED);
        forceSettlementSaveWithAuthenticationFailedCondition(user.getId(), penaltyCost);

        //then
        Inventory updatedInventory = inventoryFindService.findById(inventory.getId());
        List<Settlement> settlements = settlementService.findByUserId(user.getId());

        assertSoftly(soft -> {
            soft.assertThat(updatedInventory.getStatus()).isEqualTo(Status.AUTHENTICATION_FAILED);
            soft.assertThat(settlements.get(0).getSettlementType()).isEqualTo(Settlement.SettlementType.WITHDRAW);
            soft.assertThat(settlements.get(1).getSettlementType()).isEqualTo(Settlement.SettlementType.WITHDRAW);
            soft.assertThat(settlements.get(0).getSettlementAmount()).isEqualTo(-penaltyCost);
            soft.assertThat(settlements.get(1).getSettlementAmount()).isEqualTo(-CostType.RETURN_SHIPPING.getCost());
        });
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
        Inventory inventory = new Inventory(userId, productId, Status.IN_WAREHOUSE, address, LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    //이벤트 처리로 저장되는 상황 가정
    private void forceSettlementSaveWithAuthenticationPassedCondition(Long userId) {
        settlementService.save(userId, Settlement.SettlementType.DEPOSIT, CostType.PROTECTION.getCost());
    }

    //이벤트 처리로 저장되는 상황 가정
    private void forceSettlementSaveWithAuthenticationFailedCondition(Long userId, Long penaltyCost) {
        settlementService.save(userId, Settlement.SettlementType.WITHDRAW, -penaltyCost);
        settlementService.save(userId, Settlement.SettlementType.WITHDRAW, -CostType.RETURN_SHIPPING.getCost());
    }
}
