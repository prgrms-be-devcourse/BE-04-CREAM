package com.programmers.dev.inventory.dto;

import com.programmers.dev.common.Status;
import com.programmers.dev.inventory.domain.TransactionType;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.user.domain.Address;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record InventoryRegisterRequest(

        @NotNull(message = "상품 아이디는 NULL 값을 가질 수 없습니다.")
        Long productId,

        @NotNull
        @Min(value = 1, message = "1개 이상의 수량을 선택해야 합니다.")
        Long quantity,

        @NotBlank(message = "유효한 ZIPCODE를 입력해야 합니다.")
        String returnZipcode,

        @NotBlank(message = "유효한 반송지를 입력해야 합니다.")
        String returnAddress,

        @NotBlank(message = "유효한 상세 반송지를 입력해야 합니다.")
        String returnAddressDetail
) {

    public List<Inventory> toEntities(Long userId) {
        List<Inventory> inventories = new ArrayList<>();
        Address address = new Address(this.returnZipcode, this.returnAddress, this.returnAddressDetail);

        LocalDateTime currentTime = LocalDateTime.now();

        for (int i = 0; i < this.quantity; ++i) {
            inventories.add(new Inventory(userId, this.productId, TransactionType.SELL, Status.OUT_WAREHOUSE, address, currentTime));
        }

        return inventories;
    }
}
