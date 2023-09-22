package com.programmers.dev.Auction.dto;

import com.programmers.dev.common.AuctionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AuctionStatusChangeRequest(
    @NotNull(message = "상태 변경을 위한 경매 ID를 입력해주세요.")
    Long id,

    @NotNull(message = "변경할 경매 입찰 상태를 입력해주세요.")
    AuctionStatus auctionStatus
) {
}
