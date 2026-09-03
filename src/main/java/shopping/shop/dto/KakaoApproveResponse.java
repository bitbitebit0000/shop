package shopping.shop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class KakaoApproveResponse {

    private String aid;                 // 요청 고유 번호
    private String tid;                 // 결제 고유 번호
    private String cid;                 // 가맹점 코드
    private String partner_order_id;    // 우리 서비스의 주문번호
    private String partner_user_id;     // 회원 ID
    private String payment_method_type; // 결제 수단 (CARD 또는 MONEY)
    private Amount amount;              // 결제 금액 상세 정보
    private String item_name;           // 상품 이름
    private String approved_at;         // 결제 승인 시간
    // 결제 금액 관련 내부 클래스
    @Getter
    @Setter
    @ToString
    public static class Amount {
        private int total;              // 총 결제 금액
        private int tax_free;           // 비과세 금액
        private int vat;                // 부가가치세
        private int discount;           // 할인 금액
    }
}