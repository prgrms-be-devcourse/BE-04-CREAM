package com.programmers.dev.Auction.dto;

import java.time.LocalDateTime;

public record AuctionSaveRequest(Long productId, Long startPrice, LocalDateTime startTime, LocalDateTime endTime) {
}
