package com.programmers.dev.kream.purchasebidding.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.common.application.BankService;
import com.programmers.dev.kream.product.application.ProductService;
import com.programmers.dev.kream.product.domain.Brand;
import com.programmers.dev.kream.product.domain.Product;
import com.programmers.dev.kream.product.domain.ProductInfo;
import com.programmers.dev.kream.purchasebidding.application.PurchaseBiddingService;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseBiddingRepository;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseSelectViewDao;
import com.programmers.dev.kream.purchasebidding.ui.dto.BiddingSelectLine;
import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseSelectView;
import com.programmers.dev.kream.sellbidding.domain.SellBiddingRepository;
import com.programmers.dev.kream.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PurchaseBiddingController.class)
class PurchaseBiddingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseBiddingService purchaseBiddingService;

    @MockBean
    private PurchaseSelectViewDao productSelectViewDao;

    @MockBean
    private ProductService productService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("구매자는 입찰 구입하고 싶은 제품번호의 제품이름과 사이즈 별 최저가를 확인할 수 있다.")
    void 구입_선택화면_조회성공() throws Exception {
        //given
        Long targetProductId = 1L;
        Product product = getProduct();
        List<BiddingSelectLine> biddingSelectLines = getBiddingSelectLines();

        given(productService.findById(targetProductId)).willReturn(product);
        given(productSelectViewDao.getPurchaseView(targetProductId)).willReturn(biddingSelectLines);

        PurchaseSelectView purchaseSelectView = new PurchaseSelectView(product.getName(), biddingSelectLines);

        //when && then
        mockMvc.perform(get("/api/purchase/biddings/{productId}", targetProductId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(purchaseSelectView)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private Product getProduct() {
        return new Product(new Brand("brandA"), "아디다스 슈퍼스타", new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50000L));
    }

    private List<BiddingSelectLine> getBiddingSelectLines() {
        return List.of(
                new BiddingSelectLine(true, "220", "10", "5000"),
                new BiddingSelectLine(true, "230", "11", "6000"),
                new BiddingSelectLine(true, "240", "12", "5500"),
                new BiddingSelectLine(true, "250", "-", "-"),
                new BiddingSelectLine(true, "260", "-", "-"),
                new BiddingSelectLine(true, "270", "15", "3000"),
                new BiddingSelectLine(true, "280", "-", "-")
        );
    }
}
