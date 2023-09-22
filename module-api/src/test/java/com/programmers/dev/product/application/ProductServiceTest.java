package com.programmers.dev.product.application;

import com.programmers.dev.product.domain.Brand;
import com.programmers.dev.product.domain.BrandRepository;
import com.programmers.dev.product.domain.ProductInfo;
import com.programmers.dev.product.dto.ProductResponse;
import com.programmers.dev.product.dto.ProductSaveRequest;
import com.programmers.dev.product.dto.ProductUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Autowired
    BrandRepository brandRepository;

    @Test
    @DisplayName("상품을 저장할 수 있다")
    void saveTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        //when
        ProductResponse savedProductResponse = saveProduct(savedBrand.getId(), "jordan", productInfo, 250);

        //then
        ProductResponse findProduct = productService.findById(savedProductResponse.id());

        assertThat(findProduct.name()).isEqualTo("jordan");
    }

    @Test
    @DisplayName("상품을 Id로 조회할 수 있다")
    void findByIdTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        ProductResponse productResponse = saveProduct(savedBrand.getId(), "jordan", productInfo, 270);

        //when
        ProductResponse product = productService.findById(productResponse.id());

        //then
        assertThat(product.name()).isEqualTo("jordan");
    }

    @Test
    @DisplayName("상품 전체를 조회할 수 있다")
    void findAllTest() {
        //given
        ProductInfo productInfoA = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);
        ProductInfo productInfoB = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand brandA = new Brand("NIKE");
        Brand brandB = new Brand("ADIDAS");

        brandRepository.save(brandA);
        brandRepository.save(brandB);

        ProductResponse productResponseA = saveProduct(brandA.getId(), "Air Jordan", productInfoA, 250);
        ProductResponse productResponseB = saveProduct(brandB.getId(), "Stan-Smith", productInfoB, 260);

        //when
        List<ProductResponse> productList = productService.findAll();

        //then
        assertThat(productList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품을 수정할 수 있다")
    void updateTest() {
        //given
        Brand nike = new Brand("NIKE");
        Brand MLB = new Brand("MLB");
        Brand oldBrand = brandRepository.save(nike);
        Brand newBrand = brandRepository.save(MLB);


        ProductInfo productInfo = new ProductInfo("AAA", LocalDateTime.now(), "red", 10000L);
        ProductResponse productResponse = saveProduct(oldBrand.getId(), "jordan", productInfo, 260);

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            newBrand.getId(),
            "Dunk",
            "AAA",
            "blue");

        //when
        productService.update(productUpdateRequest);

        //then
        ProductResponse findProduct = productService.findById(productResponse.id());

        assertThat(findProduct.name()).isEqualTo("Dunk");
        assertThat(findProduct.brand().id()).isEqualTo(newBrand.getId());
        assertThat(findProduct.productInfo().getModelNumber()).isEqualTo("AAA");
    }

    private ProductResponse saveProduct(Long savedBrandId, String productName, ProductInfo productInfo, int size) {
        return productService.save(new ProductSaveRequest(
            savedBrandId,
            productName,
            productInfo,
            size));
    }
}
