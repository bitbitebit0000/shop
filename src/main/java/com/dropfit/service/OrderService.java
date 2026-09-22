package com.dropfit.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.dropfit.domain.Delivery;
import com.dropfit.domain.Member;
import com.dropfit.domain.Order;
import com.dropfit.domain.OrderItem;
import com.dropfit.domain.item.Item;
import com.dropfit.domain.item.ItemOption;
import com.dropfit.repository.ItemOptionRepository;
import com.dropfit.repository.MemberRepository;
import com.dropfit.repository.OrderRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemOptionRepository itemOptionRepository;

    @Transactional
    public Long createPendingOrder(Long memberId, Long itemOptionId, int count, String payType) {
        Member member = memberRepository.findMember(memberId);
        ItemOption itemOption = itemOptionRepository.findItemOption(itemOptionId);
        Item item = itemOption.getItem();

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        OrderItem orderItem = OrderItem.createOrderItem(itemOption, item.getPrice(), count);

        Order order = Order.createOrder(member, delivery, payType, orderItem);

        orderRepository.save(order);
        return order.getId();
    }

    @Transactional
    public void completePayment(Long orderId, String impUid, String merchantUid) {
        Order order = orderRepository.findOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found. ID: " + orderId);
        }
        order.completePayment(impUid, merchantUid);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOrder(orderId);
        order.cancel();
    }

    public Order findOrder(Long orderId) {
        return orderRepository.findOrder(orderId);
    }

    public List<Order> findOrdersByMember(Long memberId) {
        return orderRepository.findByMemberId(memberId);
    }

    public List<Order> findAll() {
        return orderRepository.findOrders();
    }
}