package com.programmers.dev.kream.product.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "BRANDS")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BRAND_NAME")
    private String name;

    protected Brand() {
    }

    public Brand(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
}
