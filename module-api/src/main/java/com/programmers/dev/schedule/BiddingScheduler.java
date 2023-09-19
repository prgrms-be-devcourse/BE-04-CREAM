package com.programmers.dev.schedule;

import com.programmers.dev.bidding.domain.Bidding;
import com.programmers.dev.bidding.domain.BiddingRepository;
import com.programmers.dev.common.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Transactional
@RequiredArgsConstructor
public class BiddingScheduler {

    private final BiddingRepository biddingRepository;

    /*
    매일 자정 기준으로 마감 기한일이 지났다면 Expire 처리
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void expireBidding() {
        biddingRepository.findLiveBidding(Status.LIVE)
                .stream()
                .filter(bidding -> bidding.getDueDate().isBefore(LocalDateTime.now()))
                .forEach(
                        Bidding::expire
                );
    }
}
