package com.programmers.dev.kream.sellbidding.application;


import com.programmers.dev.kream.common.bidding.Status;
import com.programmers.dev.kream.product.domain.Product;
import com.programmers.dev.kream.product.domain.ProductRepository;
import com.programmers.dev.kream.product.domain.SizedProduct;
import com.programmers.dev.kream.product.domain.SizedProductRepository;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBidding;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBiddingRepository;
import com.programmers.dev.kream.sellbidding.domain.SellBidding;
import com.programmers.dev.kream.sellbidding.domain.SellBiddingRepository;
import com.programmers.dev.kream.sellbidding.ui.ProductInformation;
import com.programmers.dev.kream.sellbidding.ui.SellBiddingRequest;
import com.programmers.dev.kream.sellbidding.ui.SellBiddingResponse;
import com.programmers.dev.kream.sellbidding.ui.SizeInformation;
import com.programmers.dev.kream.user.domain.User;
import com.programmers.dev.kream.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SellBiddingService {

    private final SellBiddingRepository sellBiddingRepository;
    private final PurchaseBiddingRepository purchaseBiddingRepository;
    private final SizedProductRepository sizedProductRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public SellBiddingService(SellBiddingRepository sellBiddingRepository, SizedProductRepository sizedProductRepository, UserRepository userRepository, ProductRepository productRepository, PurchaseBiddingRepository purchaseBiddingRepository) {
        this.sellBiddingRepository = sellBiddingRepository;
        this.purchaseBiddingRepository = purchaseBiddingRepository;
        this.sizedProductRepository = sizedProductRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public ProductInformation getProductInformation(Long productId) {
        ArrayList<SizeInformation> sizeInformationList = new ArrayList<>();

        Product product = productRepository.findById(productId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 productId 입니다.")
                );
        List<SizedProduct> sizedProductList = sizedProductRepository.findAllByProductId(productId);
        sizedProductList.forEach(
                sizedProduct -> {
                    List<PurchaseBidding> purchaseBiddingList = purchaseBiddingRepository.findBySizedProductId(sizedProduct.getId());

                    if (!purchaseBiddingList.isEmpty()) {
                        sizeInformationList.add(new SizeInformation(true, sizedProduct.getSize(), sizedProduct.getId(), purchaseBiddingList.get(0).getPrice().intValue()));
                    } else {
                        sizeInformationList.add(new SizeInformation(false, sizedProduct.getSize(), sizedProduct.getId(), null));
                    }
                }
        );

        return new ProductInformation(product.getName(), sizeInformationList);
    }

    @Transactional
    public SellBiddingResponse saveSellBidding(Long userId, Long sizedProductId, SellBiddingRequest sellBiddingRequest) {
        validateUserId(userId);
        SizedProduct sizedProduct = findSizedProduct(sizedProductId);

        SellBidding savedSellBidding = sellBiddingRepository.save(
                SellBidding.of(userId, sizedProduct.getId(), sellBiddingRequest)
        );

        return new SellBiddingResponse(savedSellBidding.getId());
    }

    private void validateUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("잘못된 회원 id 입니다.")
                );
    }

    private SizedProduct findSizedProduct(Long sizedProductId) {
        return sizedProductRepository.findById(sizedProductId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 상품이 존재하지 않습니다.")
                );
    }

    @Transactional
    public SellBiddingResponse transactPurchaseBidding(Long userId, Long purchaseBiddingId) {
        validateUserId(userId);
        PurchaseBidding purchaseBidding = findPurchaseBidding(purchaseBiddingId);
        validateSellUserAndPurchaseUser(userId, purchaseBidding);
        SellBidding sellBidding = SellBidding.of(userId, purchaseBidding);
        purchaseBidding.changeStatus(Status.SHIPPED);
        sellBiddingRepository.save(sellBidding);

        User seller = getUser(userId);
        User buyer = getUser(purchaseBidding.getPurchaseBidderId());

        transportMoney(seller, buyer, sellBidding.getPrice());

        return new SellBiddingResponse(sellBidding.getId());
    }

    private User getUser(Long userId) {
        User seller = userRepository.findById(userId).get();
        return seller;
    }

    private void transportMoney(User seller, User buyer, Integer price) {
        seller.deposit(price);
        buyer.withdraw(price.longValue());
    }

    private PurchaseBidding findPurchaseBidding(Long purchaseBiddingId) {
        return purchaseBiddingRepository.findById(purchaseBiddingId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 구매 입찰 정보가 없습니다.")
                );
    }

    private static void validateSellUserAndPurchaseUser(Long userId, PurchaseBidding purchaseBidding) {
        if (userId == purchaseBidding.getPurchaseBidderId()) {
            throw new IllegalArgumentException("비정상적인 접근입니다.");
        }
    }
}
