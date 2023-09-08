package com.programmers.dev.kream.product.domain;

import com.programmers.dev.kream.product.ui.dto.ProductUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class ProductInfo {

    @Column(name = "MODEL_NUMBER")
    private String modelNumber;

    @Column(name = "RELEASE_DATE")
    private LocalDateTime releaseDate;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "RELEASE_PRICE")
    private Long releasePrice;

    protected ProductInfo() {
    }

    public ProductInfo(String modelNumber, LocalDateTime releaseDate, String color, Long releasePrice) {
        this.modelNumber = modelNumber;
        this.releaseDate = releaseDate;
        this.color = color;
        this.releasePrice = releasePrice;
    }

    public ProductInfo(ProductUpdateRequest productUpdateRequest) {
        this.modelNumber = productUpdateRequest.modelNumber();
        this.color = productUpdateRequest.color();
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public String getColor() {
        return color;
    }

    public Long getReleasePrice() {
        return releasePrice;
    }
}
