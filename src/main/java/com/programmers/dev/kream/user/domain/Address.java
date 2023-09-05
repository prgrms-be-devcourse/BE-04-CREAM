package com.programmers.dev.kream.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "ZIPCODE", nullable = false)
    private String zipcode;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "ADDRESS_DETAIL", nullable = false)
    private String addressDetail;

    protected Address() {}

    public Address(String zipcode, String address, String addressDetail) {
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressDetail() {
        return addressDetail;
    }
}
