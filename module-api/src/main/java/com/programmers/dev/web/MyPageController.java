package com.programmers.dev.web;

import com.programmers.dev.settlement.application.SettlementService;
import com.programmers.dev.settlement.domain.Settlement;
import com.programmers.dev.settlement.domain.SettlementRepository;
import com.programmers.dev.settlement.query.dto.SettlementView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    private final SettlementRepository settlementRepository;

    @GetMapping("/web/myPage")
    public String myPage(Model model) {
        MyPageService.UserViewDto purchaser = myPageService.getPurchaser();
        MyPageService.UserViewDto seller = myPageService.getSeller();
        List<MyPageService.ProductView> purchaserProductViews = myPageService.selectAllForPurchaser();
        List<MyPageService.ProductView> sellerProductViews = myPageService.selectAllForSeller();

        List<Settlement> settlements = settlementRepository.findAll();


        model.addAttribute("purchaser", purchaser);
        model.addAttribute("seller", seller);
        model.addAttribute("purchaserViews", purchaserProductViews);
        model.addAttribute("sellerViews", sellerProductViews);
        model.addAttribute("settlements", settlements);

        return "myPage";
    }
}
