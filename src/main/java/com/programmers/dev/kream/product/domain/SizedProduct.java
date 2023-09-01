package com.programmers.dev.kream.product.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "SIZED_PRODUCTS")
public class SizedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Column(name = "SIZE")
    private int size;

    protected SizedProduct() {}

    public SizedProduct(Product product, int size) {
        this.product = product;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getSize() {
        return size;
    }
}
