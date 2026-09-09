package shopping.shop.dto;

import lombok.Getter;
import lombok.Setter;
import shopping.shop.domain.Member;
import shopping.shop.domain.Order;
import shopping.shop.domain.OrderItem;
import shopping.shop.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class MyPageDto {

    // 회원 프로필 정보
    private String memberName;
    private String email;
    private String city;
    private String street;
    private String zipcode;

    // 주문 및 배송 목록
    private List<OrderSummaryDto> orders;

    // 생성자: Member와 Order 리스트를 받아 DTO로 변환
    public MyPageDto(Member member, List<Order> orderList) {
        this.memberName = member.getName();

        if (member.getAddress() != null) {
            this.city = member.getAddress().getCity();
            this.street = member.getAddress().getStreet();
            this.zipcode = member.getAddress().getZipcode();
        }

        if (orderList != null) {
            this.orders = orderList.stream()
                    .map(OrderSummaryDto::new)
                    .collect(Collectors.toList());
        }
    }

    // 주문 건별 요약 DTO (내부 클래스)
    @Getter @Setter
    public static class OrderSummaryDto {
        private Long orderId;
        private String itemName;
        private int count;
        private int totalPrice;
        private OrderStatus orderStatus;

        // 배송 정보
        private String deliveryStatus;
        private String courierCompany;
        private String trackingNumber;
        private LocalDateTime orderDate;

        public OrderSummaryDto(Order order) {
            this.orderId = order.getId();

            // 첫 번째 주문 상품을 대표 상품명으로 설정 (ItemOption을 거쳐 Item 탐색)
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                OrderItem firstOrderItem = order.getOrderItems().get(0);

                if (firstOrderItem.getItemOption() != null && firstOrderItem.getItemOption().getItem() != null) {
                    String baseItemName = firstOrderItem.getItemOption().getItem().getName();
                    int extraCount = order.getOrderItems().size() - 1;

                    // 2개 이상 주문 시 "대표상품 외 N건" 처리
                    this.itemName = (extraCount > 0) ? baseItemName + " 외 " + extraCount + "건" : baseItemName;
                }

                this.count = firstOrderItem.getCount();
            }

            this.totalPrice = order.getTotalPrice();
            this.orderStatus = order.getOrderStatus();

            // 배송 정보 처리
            if (order.getDelivery() != null && order.getDelivery().getDeliveryStatus() != null) {
                this.deliveryStatus = order.getDelivery().getDeliveryStatus().name();
            }

            this.orderDate = order.getOrderDate();
        }
    }
}