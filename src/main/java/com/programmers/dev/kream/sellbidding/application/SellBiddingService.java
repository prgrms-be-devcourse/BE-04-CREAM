package com.programmers.dev.kream.sellbidding.application;

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
import com.programmers.dev.kream.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SellBiddingService {

    private final SellBiddingRepository sellBiddingRepository;
    private final SizedProductRepository sizedProductRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PurchaseBiddingRepository purchaseBiddingRepository;

    public SellBiddingService(SellBiddingRepository sellBiddingRepository, SizedProductRepository sizedProductRepository, UserRepository userRepository, ProductRepository productRepository, PurchaseBiddingRepository purchaseBiddingRepository) {
        this.sellBiddingRepository = sellBiddingRepository;
        this.sizedProductRepository = sizedProductRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.purchaseBiddingRepository = purchaseBiddingRepository;
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


    /**
     * todo : 해당 비즈니스 예외 처리 구현
     * 판매입찰 등록 비즈니스 로직
     *
     * @throws IllegalArgumentException : 회원 id 및 사이즈가 있는 상품 id가 유효하지 않을 경우 예외 발생
      */
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

}
