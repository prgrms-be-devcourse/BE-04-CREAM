package com.programmers.dev.kream.purchasebidding.application;


import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBiddingRepository;
import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseBiddingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PurchaseBiddingService {

    private final PurchaseBiddingRepository purchaseBiddingRepository;

    public PurchaseBiddingService(PurchaseBiddingRepository purchaseBiddingRepository) {
        this.purchaseBiddingRepository = purchaseBiddingRepository;
    }

    public PurchaseBiddingResponse findById(Long id) {
        PurchaseBidding purchaseBidding = purchaseBiddingRepository.findById(id).get();

        return PurchaseBiddingResponse.fromEntity(purchaseBidding);
    }
}
