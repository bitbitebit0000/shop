package shopping.shop.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import shopping.shop.domain.Member;
import shopping.shop.domain.Role;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String adminMain(HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if(loginMember == null) {
            return "redirect:/login";
        }

        if(loginMember.getRole() != Role.ADMIN) {
            return "redirect:/"; //경고메시지 넣어야함
        }

        return "admin/main";
    }


}
