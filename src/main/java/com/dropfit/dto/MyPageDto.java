package com.dropfit.dto;

import lombok.Getter;
import lombok.Setter;
import com.dropfit.domain.Member;
import com.dropfit.domain.Order;
import com.dropfit.domain.OrderItem;
import com.dropfit.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MyPageDto {

    private String memberName;
    private String email;
    private String city;
    private String street;
    private String zipcode;

    private List<OrderSummaryDto> orders;

    public MyPageDto(Member member, List<Order> orderList) {
        this.memberName = member.getName();

        if (member.getAddress() != null) {
            this.city = member.getAddress().getCity();
            this.street = member.getAddress().getStreet();
            this.zipcode = member.getAddress().getZipcode();
        }

        if (orderList != null) {
            this.orders = new ArrayList<>();
            for (Order order : orderList) {
                this.orders.add(new OrderSummaryDto(order));
            }
        }
    }

    @Getter @Setter
    public static class OrderSummaryDto {
        private Long orderId;
        private String orderNumber;
        private String itemName;
        private int count;
        private int totalPrice;
        private OrderStatus orderStatus;
        private String deliveryStatus;
        private String courierCompany;
        private String trackingNumber;
        private LocalDateTime orderDate;

        public OrderSummaryDto(Order order) {
            this.orderId = order.getId();
            this.orderNumber = order.getOrderNumber();

            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                OrderItem firstOrderItem = order.getOrderItems().get(0);

                if (firstOrderItem.getItemOption() != null && firstOrderItem.getItemOption().getItem() != null) {
                    String baseItemName = firstOrderItem.getItemOption().getItem().getName();
                    int extraCount = order.getOrderItems().size() - 1;

                    this.itemName = (extraCount > 0) ? baseItemName + " 외 " + extraCount + "건" : baseItemName;
                }

                this.count = firstOrderItem.getCount();
            }

            this.totalPrice = order.getTotalPrice();
            this.orderStatus = order.getOrderStatus();

            if (order.getDelivery() != null && order.getDelivery().getDeliveryStatus() != null) {
                this.deliveryStatus = order.getDelivery().getDeliveryStatus().name();
            }

            this.orderDate = order.getOrderDate();
        }
    }
}