package com.programmers.dev.kream.product.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "PRODUCTS",
    indexes = @Index(name = "idx_name_size",
        columnList = "product_name, size"))
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

    @Column(name = "SIZE")
    private int size;

    protected Product() {
    }

    public Product(Brand brand, String name, ProductInfo productInfo, int size) {
        this.brand = brand;
        this.name = name;
        this.productInfo = productInfo;
        this.size = size;
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

    public int getSize() {
        return size;
    }

    public void update(Brand brand, String name, ProductInfo productInfo) {
        this.brand = brand;
        this.name = name;
        this.productInfo = productInfo;
    }
}
