package shopping.shop.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import shopping.shop.domain.Address;
import shopping.shop.domain.LoginForm;
import shopping.shop.domain.MemberForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import shopping.shop.domain.Member;
import shopping.shop.service.MemberService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "members/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm, HttpServletRequest request, Model model) {
        Member loginMember = memberService.login(loginForm.getEmail(),loginForm.getPassword());
        if(loginMember == null) {
           model.addAttribute("loginError", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "members/login";
        }

        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);

        String redirectURL = (String) session.getAttribute("redirectURL");
        if(redirectURL != null) {
            session.removeAttribute("redirectURL");
            return "redirect:" + redirectURL;
        }

        return "redirect:/";
    }


    @GetMapping("/sign-up")
    public String signUp(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/sign-up";
    }

    @PostMapping("/sign-up")
    public String joinMember(MemberForm memberForm, Model model){
        Member member = new Member();
        member.setEmail(memberForm.getEmail());
        member.setPassword(memberForm.getPassword());
        member.setName(memberForm.getName());
        member.setAddress(new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode()));

        try {
            memberService.join(member);
        }catch(IllegalArgumentException e){ // |IllegalStateException
            model.addAttribute("errorMessage", e.getMessage());
            return "members/sign-up";
        }
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
       if(session != null) {
           session.invalidate();
       }
        return "redirect:/";
    }





    @GetMapping("/members") //멤버목록
    public String members(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/members";
    }
    //: 새 데이터를 만드는 기능입니다. (예: 회원가입, 글 작성)Read (읽기): 저장된 데이터를 조회하거나 읽는 기능입니다. (예: 게시글 목록 보기, 내 프로필 조회)Update (수정): 기존 데이터를 바꾸는 기능입니다. (예: 비밀번호 변경, 닉네임 수정)Delete (삭제): 필요 없는 데이터를 지우는 기능입니다. (예: 회원탈퇴, 글 삭제)
}