package com.dropfit.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Column(name = "order_number")
    private String orderNumber;

    private String tid;

    private String payType;
    private String impUid;
    private String merchantUid;

    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void addDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public static Order createOrder(Member member, Delivery delivery, String payType, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        order.setPayType(payType);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderNumber(dateStr + "-" + randomStr);

        order.setOrderDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        order.setOrderStatus(OrderStatus.BEFORE_PAYMENT);
        return order;
    }

    public void completePayment(String impUid, String merchantUid) {
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.orderStatus = OrderStatus.PAID;
    }

    public void cancel() {
        if(getDeliveryStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송된 건");
        }
        setOrderStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    public int getTotalPrice() {
        int totalPrice = 0;

        for(OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
