package com.programmers.dev.kream.product.ui.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record GetProductInfoResponse(
        Long productId,
        String brandName,
        String productName,
        String modelNumber,
        LocalDateTime releaseDate,
        String color,
        Long releasePrice,
        List<Integer> sizes
) {
    public static Optional<GetProductInfoResponse> of(List<SizedProduct> sizedProductList) {
        return convertListToRecord(sizedProductList);
    }

    private static Optional<GetProductInfoResponse> convertListToRecord(List<SizedProduct> sizedProductList) {
        if (!sizedProductList.isEmpty()) {
            SizedProduct commonSizedProductInfo = sizedProductList.get(0);
            ArrayList<Integer> sizeList = extractSizeList(sizedProductList);
            return Optional.of(
                    new GetProductInfoResponse(commonSizedProductInfo.getId(),
                            commonSizedProductInfo.getProduct().getBrand().getName(),
                            commonSizedProductInfo.getProduct().getName(),
                            commonSizedProductInfo.getProduct().getProductInfo().getModelNumber(),
                            commonSizedProductInfo.getProduct().getProductInfo().getReleaseDate(),
                            commonSizedProductInfo.getProduct().getProductInfo().getColor(),
                            commonSizedProductInfo.getProduct().getProductInfo().getReleasePrice(),
                            sizeList));
        } else {
            return Optional.empty();
        }
    }

    private static ArrayList<Integer> extractSizeList(List<SizedProduct> sizedProductList) {
        ArrayList<Integer> sizeList = new ArrayList<>();
        sizedProductList.forEach(
                sizedProduct -> sizeList.add(sizedProduct.getSize())
        );
        return sizeList;
    }
}
