package shopping.shop.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import shopping.shop.domain.Member;
import shopping.shop.domain.Order;
import shopping.shop.dto.MyPageDto;
import shopping.shop.repository.MemberRepository;
import shopping.shop.service.MemberService;
import shopping.shop.service.OrderService;

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
       return "members/mypage";
    }


}
