package com.programmers.dev.Auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuctionBidRequest(
    @NotNull(message = "경매 입찰하려는 경매 ID를 입력해주세요.")
    Long auctionId,

    @NotNull(message = "경매 입찰할 가격을 입력해주세요.")
    @Positive(message = "가격은 음수가 될 수 없습니다.")
    Long price
) {
}
