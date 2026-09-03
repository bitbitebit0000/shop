package shopping.shop.service;

import shopping.shop.domain.Delivery;
import shopping.shop.domain.Member;
import shopping.shop.domain.Order;
import shopping.shop.domain.OrderItem;
import shopping.shop.domain.item.Item;
import shopping.shop.repository.ItemRepository;
import shopping.shop.repository.MemberRepository;
import shopping.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    /**
     * 1. 결제 준비 단계: 주문 생성 (BEFORE_PAYMENT 상태)
     * Controller의 /ready 시점에 호출됩니다.
     */
    @Transactional //Order매서드명 createPendingOrder
    public Long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberRepository.findMember(memberId);
        Item item = itemRepository.findById(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성 (createOrder 내부에서 status = BEFORE_PAYMENT 설정)
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 2. 결제 완료 단계: 결제 승인 시 상태 변경 (BEFORE_PAYMENT -> PAID)
     * Controller의 /completed (또는 /success) 시점에 호출됩니다.
     */
    @Transactional
    public void completeOrder(Long orderId, String tid) {
        Order order = orderRepository.findOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("존재하지 않는 주문입니다. id=" + orderId);
        }

        // 주문 엔티티의 비즈니스 메서드 호출 (상태를 PAID로 변경하고 tid 저장)
        order.completePayment(tid);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findOrder(orderId);
        order.cancel();
    }

    /**
     * 단건 주문 조회
     */
    public Order findOrder(Long orderId) {
        return orderRepository.findOrder(orderId);
    }

    /**
     * 회원별 주문 목록 조회
     */
    public List<Order> findOrdersByMember(Long memberId) {
        return orderRepository.findAll(memberId);
    }
}