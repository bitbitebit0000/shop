package com.dropfit.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.dropfit.domain.Member;
import com.dropfit.domain.Order;
import com.dropfit.dto.MyPageDto;
import com.dropfit.service.MemberService;
import com.dropfit.service.OrderService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final OrderService orderService;
    private final MemberService memberService;

    @GetMapping("/mypage")
    public String myPage(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");
        if(loginMember == null) {
            String requestURI = request.getRequestURI();
            session.setAttribute("redirectURL", requestURI);
            return "redirect:/login";
        }

       Member member = memberService.findMember(loginMember.getId());
       List<Order> orders = orderService.findOrdersByMember(member.getId());
       MyPageDto myPageData = new MyPageDto(member, orders);
       model.addAttribute("myPage", myPageData);
       return "member/mypage";
    }
}
