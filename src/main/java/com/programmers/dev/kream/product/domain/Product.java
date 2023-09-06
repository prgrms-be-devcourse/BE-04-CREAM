package com.programmers.dev.kream.product.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "PRODUCTS")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRAND_ID")
    private Brand brand;

    @Column(name = "PRODUCT_NAME")
    private String name;

    @Embedded
    private ProductInfo productInfo;

    protected Product() {
    }

    public Product(Brand brand, String name, ProductInfo productInfo) {
        this.brand = brand;
        this.name = name;
        this.productInfo = productInfo;
    }

    public Long getId() {
        return id;
    }

    public Brand getBrand() {
        return brand;
    }

    public String getName() {
        return name;
    }

    public ProductInfo getProductInfo() {
        return productInfo;
    }

    public void update(Brand brand, String name, ProductInfo productInfo) {
        this.brand = brand;
        this.name = name;
        this.productInfo = productInfo;
    }
}
