package com.programmers.dev.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/web/myPage")
    public String myPage(Model model) {
        MyPageService.UserViewDto purchaser = myPageService.getPurchaser();
        MyPageService.UserViewDto seller = myPageService.getSeller();
        List<MyPageService.ProductView> purchaserProductViews = myPageService.selectAllForPurchaser();
        List<MyPageService.ProductView> sellerProductViews = myPageService.selectAllForSeller();

        model.addAttribute("purchaser", purchaser);
        model.addAttribute("seller", seller);
        model.addAttribute("purchaserViews", purchaserProductViews);
        model.addAttribute("sellerViews", sellerProductViews);

        return "myPage";
    }
}
