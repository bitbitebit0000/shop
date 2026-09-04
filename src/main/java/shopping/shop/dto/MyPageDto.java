package shopping.shop.dto;

import lombok.Getter;
import lombok.Setter;
import shopping.shop.domain.Member;
import shopping.shop.domain.Order;
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

        // Member 엔티티에 email 필드가 없다면 주석 처리 또는 적절한 값 설정
        // this.email = member.getEmail();

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
        private OrderStatus orderStatus; // BEFORE_PAYMENT, PAID 등

        // 배송 정보
        private String deliveryStatus;   // READY, SHIPPED, DELIVERED
        private String courierCompany;   // 택배사명 (예: 야마토, CJ대한통운)
        private String trackingNumber;   // 운송장 번호
        private LocalDateTime orderDate;

        public OrderSummaryDto(Order order) {
            this.orderId = order.getId();

            // 첫 번째 주문 상품을 대표 상품명으로 설정
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                this.itemName = order.getOrderItems().get(0).getItem().getName();
                this.count = order.getOrderItems().get(0).getCount();
            }

            this.totalPrice = order.getTotalPrice(); // Order 내 총금액 계산 메서드 활용
            this.orderStatus = order.getOrderStatus();

            // 배송 정보 처리
            if (order.getDelivery() != null) {
                if (order.getDelivery().getDeliveryStatus() != null) {
                    this.deliveryStatus = order.getDelivery().getDeliveryStatus().name();
                }
              //  this.courierCompany = order.getDelivery().getCourierCompany(); 미구현로직
              //  this.trackingNumber = order.getDelivery().getTrackingNumber();
            }

            this.orderDate = order.getOrderDate();
        }
    }
}