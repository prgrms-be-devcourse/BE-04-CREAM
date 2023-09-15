package com.programmers.dev.Auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuctionBiddingCancelRequest(

    @NotNull(message = "취소할 경매 입찰 ID를 입력해주세요.")
    Long auctionId,

    @NotNull(message = "취소할 입찰의 가격을 입력해주세요.")
    @Positive(message = "가격은 음수가 될 수 없습니다.")
    Long price
) {
}
