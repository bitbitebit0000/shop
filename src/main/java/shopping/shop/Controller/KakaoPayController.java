package shopping.shop.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shopping.shop.dto.KakaoApproveResponse;
import shopping.shop.dto.KakaoReadyResponse;
import shopping.shop.domain.item.Item;
import shopping.shop.domain.Order;
import shopping.shop.repository.ItemRepository;
import shopping.shop.service.KakaoPayService;
import shopping.shop.service.OrderService;

@Slf4j
@Controller
@RequestMapping("/order/pay")
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;
    private final ItemRepository itemRepository;
    private final OrderService orderService;

    /**
     * 1. 결제 준비 요청
     */
    @PostMapping("/ready")
    public String payReady(@RequestParam("itemId") Long itemId,
                           @RequestParam("count") int count) {

        // 1) DB에서 실제 상품 정보 조회 (카카오페이에 보낼 상품명, 금액 계산용)
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다. id=" + itemId);
        }

        String itemName = item.getName();
        int totalAmount = item.getPrice() * count;

        // 임시 회원 ID (로그인 연동 전이므로 1L 사용, 세션/시큐리티 도입 시 로그인 회원 ID로 교체)
        Long memberId = 1L;

        // 2) [수정] OrderService 파라미터 규격(memberId, itemId, count)에 맞춰 호출
        Long orderId = orderService.order(memberId, itemId, count);

        log.info("결제 준비 요청 - 주문ID: {}, 상품명: {}, 수량: {}개, 총금액: {}원", orderId, itemName, count, totalAmount);

        // 3) 카카오페이 Service 호출 (생성된 orderId 전달)
        KakaoReadyResponse readyResponse = kakaoPayService.kakaoPayReady(itemName, totalAmount, orderId);

        // 4) 카카오페이 결제 페이지로 리다이렉트
        return "redirect:" + readyResponse.getNext_redirect_pc_url();
    }

    /**
     * 2. 결제 완료 콜백
     */
    @GetMapping("/completed")
    public String payCompleted(@RequestParam("pg_token") String pgToken,
                               @RequestParam("order_id") Long orderId,
                               Model model) {

        // 1) 카카오페이 승인 요청
        KakaoApproveResponse approveResponse = kakaoPayService.kakaoPayApprove(pgToken, orderId);

        // 2) DB 주문 상태 변경: BEFORE_PAYMENT -> PAID (TID 저장)
        orderService.completeOrder(orderId, approveResponse.getTid());

        log.info("결제 승인 완료 및 DB 상태 변경(PAID) 성공 - 주문ID: {}, TID: {}", orderId, approveResponse.getTid());

        model.addAttribute("info", approveResponse);
        return "order/paySuccess";
    }

    @GetMapping("/cancel")
    public String payCancel() {
        return "order/payCancel";
    }

    @GetMapping("/fail")
    public String payFail() {
        return "order/payFail";
    }
}