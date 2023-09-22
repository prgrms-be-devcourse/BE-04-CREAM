package com.programmers.dev.Auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AuctionSaveRequest(
    @NotNull(message = "상품 ID는 NULL 값을 가질 수 없습니다.")
    Long productId,

    @NotNull(message = "경매의 시작가를 입력해주세요.")
    @Positive(message = "가격은 음수가 될 수 없습니다.")
    Long startPrice,

    @NotNull(message = "경매의 시작 시간을 입력해주세요.")
    LocalDateTime startTime,

    @NotNull(message = "경매의 마감 시간을 입력해주세요.")
    LocalDateTime endTime) {
}
