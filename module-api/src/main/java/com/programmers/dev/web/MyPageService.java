package com.programmers.dev.web;

import com.programmers.dev.Auction.application.AuctionBiddingService;
import com.programmers.dev.Auction.domain.Auction;
import com.programmers.dev.Auction.domain.AuctionBidding;
import com.programmers.dev.Auction.domain.AuctionBiddingRepository;
import com.programmers.dev.Auction.domain.AuctionRepository;
import com.programmers.dev.Auction.dto.BiddingPriceGetRequest;
import com.programmers.dev.Auction.dto.BiddingPriceGetResponse;
import com.programmers.dev.bidding.domain.Bidding;
import com.programmers.dev.bidding.domain.BiddingRepository;
import com.programmers.dev.common.AuctionStatus;
import com.programmers.dev.inventory.application.InventoryFindService;
import com.programmers.dev.inventory.domain.Inventory;
import com.programmers.dev.inventory.domain.InventoryRepository;
import com.programmers.dev.inventoryorder.application.InvenotryOrderFindService;
import com.programmers.dev.inventoryorder.domain.InventoryOrder;
import com.programmers.dev.inventoryorder.domain.InventoryOrderRepository;
import com.programmers.dev.product.domain.Product;
import com.programmers.dev.product.domain.ProductRepository;
import com.programmers.dev.user.application.UserFindService;
import com.programmers.dev.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.programmers.dev.web.MyPageService.ProductView.DomainType.*;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private static final Long PURCHASER = 1L;
    private static final Long SELLER = 2L;

    private final UserFindService userFindService;
    private final BiddingRepository biddingRepository;
    private final AuctionBiddingRepository auctionBiddingRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryOrderRepository inventoryOrderRepository;
    private final ProductRepository productRepository;

    public UserViewDto getPurchaser() {
        User purchaser = userFindService.findById(PURCHASER);
        return new UserViewDto(purchaser.getId(), purchaser.getAccount());
    }

    public UserViewDto getSeller() {
        User seller = userFindService.findById(SELLER);
        return new UserViewDto(seller.getId(), seller.getAccount());
    }

    public List<ProductView> selectAllForPurchaser() {
        List<ProductView> viewList = new ArrayList<>();
        User purchaser = userFindService.findById(PURCHASER);

        // bidding
        List<Bidding> biddingList = biddingRepository.findByUserId(PURCHASER);
        if (!biddingList.isEmpty()) {
            biddingList.forEach(
                    bidding -> {
                        Product product = productRepository.findById(bidding.getProductId()).get();
                        viewList.add(new ProductView(BIDDING, product.getName(), bidding.getStatus().toString(), (long) bidding.getPrice()));
                    }
            );
        }

        // inventoryOrder
        List<InventoryOrder> inventoryOrders =  inventoryOrderRepository.findAllByUserId(PURCHASER);
        if (!inventoryOrders.isEmpty()) {
            inventoryOrders.forEach(
                    inventoryOrder -> {
                        Inventory inventory = inventoryRepository.findById(inventoryOrder.getInventoryId()).get();
                        Product product = productRepository.findById(inventory.getProductId()).get();
                        viewList.add(new ProductView(INVENTORY, product.getName(), inventoryOrder.getInventoryOrderStatus().toString(), inventoryOrder.getOrderdPrice()));
                    }
            );
        }

        // auction
        Optional<AuctionBidding> optionalAuctionBidding = auctionBiddingRepository.findTopBiddingPrice(1L);
        if (optionalAuctionBidding.isPresent()) {
            if (optionalAuctionBidding.get().getAuction()!=null) {
                AuctionBidding auctionBidding = optionalAuctionBidding.get();
                viewList.add(
                        new ProductView(AUCTION,
                                auctionBidding.getAuction().getProduct().getName(),
                                auctionBidding.getAuction().getAuctionStatus().toString(),
                                auctionBidding.getPrice()
                        )
                );
            }
        }




        return viewList;
    }

    public List<ProductView> selectAllForSeller() {
        List<ProductView> viewList = new ArrayList<>();
        User seller = userFindService.findById(SELLER);

        // bidding
        List<Bidding> biddingList = biddingRepository.findByUserId(SELLER);
        if (!biddingList.isEmpty()) {
            biddingList.forEach(
                    bidding -> {
                        Product product = productRepository.findById(bidding.getProductId()).get();
                        viewList.add(new ProductView(BIDDING, product.getName(), bidding.getStatus().toString(), (long) bidding.getPrice()));
                    }
            );
        }

        // inventory
        List<Inventory> inventories =  inventoryRepository.findAllByUserId(SELLER);
        if (!inventories.isEmpty()) {
            inventories.forEach(
                    inventory -> {
                        Product product = productRepository.findById(inventory.getProductId()).get();
                        viewList.add(new ProductView(INVENTORY, product.getName(), inventory.getStatus().toString(), inventory.getPrice()));
                    }
            );
        }

        return viewList;
    }




    @Getter
    public static class ProductView {
        public enum DomainType {
            BIDDING, AUCTION, INVENTORY
        }

        DomainType domainType;
        String productName;
        String status;
        Long price;

        ProductView(DomainType domainType, String productName, String status,Long price) {
            this.domainType = domainType;
            this.productName = productName;
            this.status = status;
            this.price = price;
        }
    }

    @Getter
    public static class UserViewDto {
        Long id;
        Long account;

        UserViewDto(Long id, Long account)  {
            this.id = id;
            this.account = account;
        }
    }
}
