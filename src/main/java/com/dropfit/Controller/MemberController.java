package com.dropfit.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.dropfit.domain.Address;
import com.dropfit.domain.LoginForm;
import com.dropfit.domain.MemberForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.dropfit.domain.Member;
import com.dropfit.service.MemberService;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "member/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm, HttpServletRequest request, Model model) {
        Member loginMember = memberService.login(loginForm.getEmail(), loginForm.getPassword());
        if (loginMember == null) {
            model.addAttribute("loginError", "Invalid email or password.");
            return "member/login";
        }

        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);

        String redirectURL = (String) session.getAttribute("redirectURL");
        if (redirectURL != null) {
            session.removeAttribute("redirectURL");
            return "redirect:" + redirectURL;
        }

        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String joinMember(MemberForm memberForm, Model model) {
        Member member = new Member();
        member.setEmail(memberForm.getEmail());
        member.setPassword(memberForm.getPassword());
        member.setName(memberForm.getName());
        member.setAddress(new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode()));

        try {
            memberService.join(member);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/signup";
        }
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/members")
    public String members(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/members";
    }
}