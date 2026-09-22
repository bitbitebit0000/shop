package com.dropfit.Controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dropfit.domain.Member;
import com.dropfit.domain.Role;
import com.dropfit.service.MemberService;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping("/admin/members")
    public String members(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            String requestURI = request.getRequestURI();
            session.setAttribute("redirectURL", requestURI);
            return "redirect:/login";
        }

        if (loginMember.getRole() != Role.ADMIN) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied. Administrator privileges are required.");
            return "redirect:/";
        }

        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "admin/members";
    }
}