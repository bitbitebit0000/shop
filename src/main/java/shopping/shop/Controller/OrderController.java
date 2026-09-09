package shopping.shop.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shopping.shop.domain.ItemForm;
import shopping.shop.domain.Member;
import shopping.shop.domain.Order;
import shopping.shop.domain.item.Item;
import shopping.shop.exception.NotEnoughStockException;
import shopping.shop.service.ItemService;
import shopping.shop.service.MemberService;
import shopping.shop.service.OrderService;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    public String order() {
        return "order";
    }

    @GetMapping("/ordr")
    public String createItemForm(@RequestParam(name = "itemId", required = false) Long id, Model model) {
        ItemForm itemForm = new ItemForm();
        if (id != null) {
            itemForm.setItemId(id);
        }
        model.addAttribute("itemForm", itemForm);
        return "item/createItemForm";
    }

    @GetMapping("/order")
    public String createForm(@RequestParam(value = "itemId", required = false) Long itemId,
                             HttpServletRequest request,
                             Model model) {
        // 1. 로그인 검증
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            session.setAttribute("redirectURL", request.getRequestURI());
            return "redirect:/login";
        }

        // 2. 전체 상품 목록 조회
        List<Item> items = itemService.findItems();
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("items", items);

        // 3. 상품이 선택된 경우 해당 상품의 옵션 목록만 모델에 담음
        if (itemId != null) {
            Item selectedItem = itemService.findItem(itemId);
            model.addAttribute("selectedItemId", itemId);
            model.addAttribute("options", selectedItem.getOptions());
        }

        return "order/orderForm";
    }



    @PostMapping("/order/checkout")
    public String orderCheckout(@RequestParam("itemId") Long itemId,
                                @RequestParam("itemOptionId") Long itemOptionId,
                                @RequestParam("count") int count,
                                HttpServletRequest request,
                                Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        Item item = itemService.findItem(itemId);
        int totalPrice = item.getPrice() * count;

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("item", item);
        model.addAttribute("itemOptionId", itemOptionId); // payForm의 hidden 태그용
        model.addAttribute("count", count);
        model.addAttribute("totalPrice", totalPrice);

        return "order/payForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("itemId") Long itemId,
                        @RequestParam("itemOptionId") Long itemOptionId,
                        @RequestParam("count") int count,
                        @RequestParam(value = "payType", defaultValue = "CARD") String payType,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        try {
            Long orderId = orderService.order(loginMember.getId(), itemOptionId, count);
            orderService.completePayment(orderId); //여기서 주문은 끝난건데 결제를 했다고 확정을 하면 안되지않나
            return "redirect:/order/complete/" + orderId + "?payType=" + payType;

        } catch(NotEnoughStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "재고가 부족합니다.");
            return "redirect:/order?itemId=" + itemId; //에러메시지 html에 등록
        }
    }

    @GetMapping("/order/complete/{orderId}")
    public String orderComplete(@PathVariable("orderId") Long orderId,
                                @RequestParam(value = "payType", defaultValue = "CARD") String payType,
                                HttpServletRequest request,
                                Model model,
                                RedirectAttributes redirectAttributes) {
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

        Order order = orderService.findOrder(orderId);

        if (!order.getMember().getId().equals(loginMember.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "본인의 주문정보가 아닙니다.");
            return "redirect:/";
        }

        model.addAttribute("order", order);
        model.addAttribute("payType", payType);
        return "order/orderComplete";
    }

    @GetMapping("/orders")
    public String orders(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if(loginMember == null) {
            String requestURL = request.getRequestURI();
            session.setAttribute("requestURL", requestURL);
            return "redirect:/login";
        }

        List<Order> orders = orderService.findOrdersByMember(loginMember.getId());
        model.addAttribute("orders", orders);

        return "order/orders";
    }


}









    /*
    @GetMapping("/orders")
    public String orderList(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/login";
        }

        // 로그인한 회원의 전체 주문 내역 조회 (OrderSearch 등을 활용)
        List<Order> orders = orderService.findOrdersByMember(loginMember.getId());

        model.addAttribute("orders", orders);

        return "order/orderList";
    }


     */
  /*
    if (!order.getMember().getId().equals(loginMember.getId())) {
    throw new IllegalStateException("타인의 주문 내역은 조회할 수 없습니다.");
}
     */



    /*
    @ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/orders";
    }
}
     */

    /*
    if (!order.getMember().getId().equals(loginMember.getId())) {
    // 403 Forbidden 에러 던지기
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 주문에 대한 접근 권한이 없습니다.");
}
     */
