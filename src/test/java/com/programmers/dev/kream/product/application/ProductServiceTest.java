package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.product.domain.Brand;
import com.programmers.dev.kream.product.domain.BrandRepository;
import com.programmers.dev.kream.product.domain.Product;
import com.programmers.dev.kream.product.domain.ProductInfo;
import com.programmers.dev.kream.product.ui.ProductUpdateRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    BrandRepository brandRepository;
    @Autowired
    EntityManager em;

    @Test
    void saveTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        //when
        Long productId = productService.save(savedBrand.getId(), "jordan", productInfo);

        //then
        Product findProduct = productService.findById(productId);

        assertThat(findProduct.getName()).isEqualTo("jordan");
    }

    @Test
    void deleteTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        Long productId = productService.save(savedBrand.getId(), "jordan", productInfo);

        //when
        productService.delete(productId);

        //then
        assertThatThrownBy(() -> productService.findById(productId)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void findByIdTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        Long productId = productService.save(savedBrand.getId(), "jordan", productInfo);

        //when
        Product product = productService.findById(productId);

        //then
        assertThat(product.getName()).isEqualTo("jordan");
    }

    @Test
    void findAllTest() {
        //given

        //when
        List<Product> allProduct = productService.findAll();

        //then
        assertThat(allProduct.size()).isEqualTo(2);
    }

    @Test
    void updateTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand MLB = new Brand("MLB");
        Brand oldBrand = brandRepository.save(nike);
        Brand newBrand = brandRepository.save(MLB);

        Long productId = productService.save(oldBrand.getId(), "jordan", productInfo);

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            newBrand.getId(),
            "Dunk",
            "bbbb",
            productInfo.getReleaseDate(),
            "blue",
            5000L);

        //when
        productService.update(productId, productUpdateRequest);

        //then
        em.flush();
        em.clear();

        Product findProduct = productService.findById(productId);

        assertThat(findProduct.getName()).isEqualTo("Dunk");
        assertThat(findProduct.getBrand().getId()).isEqualTo(newBrand.getId());
    }
}