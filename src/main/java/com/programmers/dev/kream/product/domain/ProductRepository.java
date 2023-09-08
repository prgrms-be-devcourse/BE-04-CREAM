package com.programmers.dev.kream.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    @Query("select p from Product p where p.name =:productName and p.brand.name = :brandName")
    List<Product> findAllByNameAndBrand(@Param("productName") String productName, @Param("brandName") String brandName);
}
