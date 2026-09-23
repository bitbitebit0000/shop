package com.dropfit.Controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.dropfit.domain.Member;
import com.dropfit.domain.Order;
import com.dropfit.domain.OrderStatus;
import com.dropfit.domain.Role;
import com.dropfit.service.ItemService;
import com.dropfit.service.MemberService;
import com.dropfit.service.OrderService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AdminDashboardController {

    private final OrderService orderService;
    private final ItemService itemService;
    private final MemberService memberService;

    @GetMapping("/admin")
    @Transactional(readOnly = true)
    public String adminMain(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/login";
        }

        if (loginMember.getRole() != Role.ADMIN) {
            return "redirect:/";
        }

        List<Order> orders = orderService.findAll();

        int totalOrders = orders.size();

        int totalSales = 0;
        for (Order o : orders) {
            if (o.getOrderStatus() != null && o.getOrderStatus() != OrderStatus.CANCEL) {
                totalSales += o.getTotalPrice();
            }
        }

        int totalItems = itemService.findItems().size();
        int totalMembers = memberService.findMembers().size();

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("orders", orders);

        return "admin/dashboard";
    }
}