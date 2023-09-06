package com.programmers.dev.kream.product.application;

import com.programmers.dev.kream.exception.CreamException;
import com.programmers.dev.kream.product.domain.Brand;
import com.programmers.dev.kream.product.domain.BrandRepository;
import com.programmers.dev.kream.product.domain.ProductInfo;
import com.programmers.dev.kream.product.ui.dto.ProductResponse;
import com.programmers.dev.kream.product.ui.dto.ProductSaveRequest;
import com.programmers.dev.kream.product.ui.dto.ProductUpdateRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    @DisplayName("상품을 저장할 수 있다")
    void saveTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        //when
        ProductResponse savedProductResponse = saveProduct(savedBrand.getId(), "jordan", productInfo);

        //then
        ProductResponse findProduct = productService.findById(savedProductResponse.id());

        assertThat(findProduct.name()).isEqualTo("jordan");
    }

    @Test
    @DisplayName("상품을 삭제할 수 있다")
    void deleteTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        ProductResponse productResponse = saveProduct(savedBrand.getId(), "jordan", productInfo);

        //when
        productService.deleteById(productResponse.id());

        //then
        assertThatThrownBy(() -> productService.findById(productResponse.id())).isInstanceOf(CreamException.class);
    }

    @Test
    @DisplayName("상품을 Id로 조회할 수 있다")
    void findByIdTest() {
        //given
        ProductInfo productInfo = new ProductInfo("aaa", LocalDateTime.now(), "red", 1000L);

        Brand nike = new Brand("NIKE");
        Brand savedBrand = brandRepository.save(nike);

        ProductResponse productResponse = saveProduct(savedBrand.getId(), "jordan", productInfo);

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

        ProductResponse productResponseA = saveProduct(brandA.getId(), "Air Jordan", productInfoA);
        ProductResponse productResponseB = saveProduct(brandB.getId(), "Stan-Smith", productInfoB);

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

        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            newBrand.getId(),
            "Dunk",
            "bbbb",
            "blue",
            5000L);

        ProductInfo productInfo = new ProductInfo(productUpdateRequest, LocalDateTime.now());

        ProductResponse productResponse = saveProduct(oldBrand.getId(), "jordan", productInfo);

        //when
        productService.update(productResponse.id(), productUpdateRequest);

        //then
        em.flush();
        em.clear();

        ProductResponse findProduct = productService.findById(productResponse.id());

        assertThat(findProduct.name()).isEqualTo("Dunk");
        assertThat(findProduct.brand().id()).isEqualTo(newBrand.getId());
    }

    private ProductResponse saveProduct(Long savedBrandId, String productName, ProductInfo productInfo) {
        return productService.save(new ProductSaveRequest(
            savedBrandId,
            productName,
            productInfo));
    }
}
