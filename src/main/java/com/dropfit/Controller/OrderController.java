package com.dropfit.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import com.dropfit.domain.Member;
import com.dropfit.domain.Order;
import com.dropfit.domain.item.Item;
import com.dropfit.domain.item.ItemOption;
import com.dropfit.exception.NotEnoughStockException;
import com.dropfit.service.ItemService;
import com.dropfit.service.OrderService;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ItemService itemService;

    @GetMapping("")
    public String createForm(@RequestParam(value = "itemId", required = false) Long itemId,
                             HttpServletRequest request,
                             Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            session.setAttribute("redirectURL", request.getRequestURI());
            return "redirect:/login";
        }
        List<Item> items = itemService.findItems();
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("items", items);

        if (itemId != null) {
            Item selectedItem = itemService.findItem(itemId);
            model.addAttribute("selectedItemId", itemId);
            model.addAttribute("options", selectedItem.getOptions());
        }

        return "order/form";
    }

    @PostMapping("/checkout")
    public String orderCheckout(@RequestParam("itemId") Long itemId,
                                @RequestParam(value = "itemOptionId", required = false) Long itemOptionId,
                                @RequestParam("count") int count,
                                HttpServletRequest request,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        try {
            Item item = itemService.findItem(itemId);

            ItemOption selectedOption = null;
            for (ItemOption option : item.getOptions()) {
                if (option.getId().equals(itemOptionId)) {
                    selectedOption = option;
                    break;
                }
            }

            if (selectedOption == null) {
                throw new IllegalArgumentException("The selected option does not exist.");
            }

            if (selectedOption.getStockQuantity() < count) {
                throw new NotEnoughStockException("Not enough stock. (Current remaining stock: " + selectedOption.getStockQuantity() + " pcs)");
            }

            int totalPrice = item.getPrice() * count;

            model.addAttribute("loginMember", loginMember);
            model.addAttribute("item", item);
            model.addAttribute("itemOptionId", itemOptionId);
            model.addAttribute("count", count);
            model.addAttribute("totalPrice", totalPrice);

            return "order/checkout";

        } catch (NotEnoughStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/order?itemId=" + itemId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "redirect:/order?itemId=" + itemId;
        }
    }

    @PostMapping("")
    public String order(@RequestParam("itemId") Long itemId,
                        @RequestParam("itemOptionId") Long itemOptionId,
                        @RequestParam("count") int count,
                        @RequestParam(value = "payType", defaultValue = "CARD") String payType,
                        @RequestParam(value = "paymentId", required = false) String paymentId,
                        @RequestParam(value = "merchantUid", required = false) String merchantUid,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {

        HttpSession session = request.getSession(false);
        Member loginMember = (session != null) ? (Member) session.getAttribute("loginMember") : null;

        if (loginMember == null) {
            return "redirect:/login";
        }

        try {
            Long orderId = orderService.createPendingOrder(loginMember.getId(), itemOptionId, count, payType);
            if (!"BANK".equals(payType)) {
                if (paymentId == null || paymentId.isBlank()) {
                    throw new IllegalStateException("Payment approval ID (paymentId) is missing.");
                }
                orderService.completePayment(orderId, paymentId, merchantUid);
            }
            return "redirect:/order/complete/" + orderId + "?payType=" + payType;
        } catch (NotEnoughStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Not enough stock.");
            return "redirect:/order?itemId=" + itemId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred during payment processing: " + e.getMessage());
            return "redirect:/order?itemId=" + itemId;
        }
    }

    @GetMapping("/complete/{orderId}")
    public String orderComplete(@PathVariable("orderId") String orderId,
                                @RequestParam(value = "payType", defaultValue = "CARD") String payType,
                                HttpServletRequest request,
                                Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            String requestURI = request.getRequestURI();
            if (request.getQueryString() != null) {
                requestURI += "?" + request.getQueryString();
            }
            session.setAttribute("redirectURL", requestURI);
            return "redirect:/login";
        }

        Order order = orderService.findOrder(Long.valueOf(orderId));

        model.addAttribute("order", order);
        model.addAttribute("payType", payType);
        return "order/order-complete";
    }

    @GetMapping("/orders")
    public String orders(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            String requestURL = request.getRequestURI();
            session.setAttribute("requestURL", requestURL);
            return "redirect:/login";
        }

        List<Order> orders = orderService.findOrdersByMember(loginMember.getId());
        model.addAttribute("orders", orders);

        return "order/orders";
    }

    @GetMapping("/mobile-callback")
    public String mobileOrderCallback(@RequestParam("paymentId") String paymentId,
                                      @RequestParam(value = "payType", defaultValue = "CARD") String payType,
                                      @RequestParam(value = "itemId", required = false) Long itemId,
                                      @RequestParam(value = "itemOptionId", required = false) Long itemOptionId,
                                      @RequestParam(value = "count", defaultValue = "1") int count,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {

        HttpSession session = request.getSession(false);
        Member loginMember = (session != null) ? (Member) session.getAttribute("loginMember") : null;

        if (loginMember == null) {
            return "redirect:/login";
        }

        try {
            Long orderId = orderService.createPendingOrder(loginMember.getId(), itemOptionId, count, payType);
            orderService.completePayment(orderId, paymentId, null);

            return "redirect:/order/complete/" + orderId + "?payType=" + payType;

        } catch (NotEnoughStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Not enough stock.");
            return "redirect:/order?itemId=" + itemId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred during payment processing: " + e.getMessage());
            return "redirect:/order?itemId=" + itemId;
        }
    }
}

