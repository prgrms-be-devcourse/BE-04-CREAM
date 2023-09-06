package com.programmers.dev.kream.purchasebidding.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.dev.kream.product.application.ProductService;
import com.programmers.dev.kream.product.domain.ProductInfo;
import com.programmers.dev.kream.product.ui.dto.BrandResponse;
import com.programmers.dev.kream.product.ui.dto.ProductResponse;
import com.programmers.dev.kream.purchasebidding.application.PurchaseBiddingService;
import com.programmers.dev.kream.purchasebidding.domain.PurchaseSelectViewDao;
import com.programmers.dev.kream.purchasebidding.ui.dto.BiddingSelectLine;
import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseSelectView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@ExtendWith(RestDocumentationExtension.class)
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

    @BeforeEach
    void setup(
        WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentationContextProvider
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .apply(documentationConfiguration(restDocumentationContextProvider)
                .operationPreprocessors()
                    .withRequestDefaults(modifyUris().host("13.125.254.94"), prettyPrint())
                    .withResponseDefaults(modifyUris().host("13.125.254.94"), prettyPrint()))
            .build();
    }

    @Test
    @DisplayName("구매자는 입찰 구입하고 싶은 제품번호의 제품이름과 사이즈 별 최저가를 확인할 수 있다.")
    void 구입_선택화면_조회성공() throws Exception {
        //given
        Long targetProductId = 1L;
        ProductResponse product = getProduct(targetProductId);
        List<BiddingSelectLine> biddingSelectLines = getBiddingSelectLines();

        given(productService.findById(targetProductId)).willReturn(product);
        given(productSelectViewDao.getPurchaseView(targetProductId)).willReturn(biddingSelectLines);

        PurchaseSelectView purchaseSelectView = new PurchaseSelectView(product.name(), biddingSelectLines);

        //when && then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/purchase/biddings/{productId}", targetProductId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(purchaseSelectView)))
                .andDo(print())
                .andDo(document("purchase-bid-select-view",
                        pathParameters(
                                parameterWithName("productId").description("제품 ID")
                        ),
                        responseHeaders(
                                headerWithName(CONTENT_TYPE).description("Content-Type")
                        ),
                        responseFields(
                                fieldWithPath("productName").type(JsonFieldType.STRING).description("제품 이름"),
                                fieldWithPath("biddingSelectLines[].lived").type(JsonFieldType.BOOLEAN).description("입찰 상태"),
                                fieldWithPath("biddingSelectLines[].size").type(JsonFieldType.STRING).description("사이즈"),
                                fieldWithPath("biddingSelectLines[].sizedProductId").type(JsonFieldType.STRING).description("상품 ID"),
                                fieldWithPath("biddingSelectLines[].price").type(JsonFieldType.STRING).description("가격")
                        )));
    }

    private ProductResponse getProduct(Long targetProductId) {
        return new ProductResponse(targetProductId, new BrandResponse(1L, "ADIDAS"), "SUPER-STAR", new ProductInfo("ADI-001", LocalDateTime.now(), "BLACK", 50000L));
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
