package shopping.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import shopping.shop.dto.KakaoApproveResponse;
import shopping.shop.dto.KakaoReadyResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

    @Value("${kakao.pay.admin-key}")
    private String adminKey;

    @Value("${kakao.pay.cid}")
    private String cid;

    // 카카오페이 결제 고유번호(tid)를 승인 단계에서 사용하기 위해 임시 보관
    private KakaoReadyResponse kakaoReady;

    /**
     * 1. 결제 준비 (Ready API)
     */
    public KakaoReadyResponse kakaoPayReady(String itemName, int totalAmount, Long orderId) {

        // 1) HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + adminKey);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 2) HTTP 요청 파라미터(Body) 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);                                    // 가맹점 코드 (TC0ONETIME)
        params.add("partner_order_id", String.valueOf(orderId));   // 내 서버의 주문번호
        params.add("partner_user_id", "user123");                  // 주문한 사용자 ID (임시)
        params.add("item_name", itemName);                         // 상품명
        params.add("quantity", "1");                               // 수량
        params.add("total_amount", String.valueOf(totalAmount));   // total 금액
        params.add("tax_free_amount", "0");                        // 비과세 금액

        // 결제 인증 상태별 리다이렉트 URL
        params.add("approval_url", "http://localhost:8080/order/pay/completed?order_id=" + orderId); // 인증 성공 시
        params.add("cancel_url", "http://localhost:8080/order/pay/cancel");                         // 인증 취소 시
        params.add("fail_url", "http://localhost:8080/order/pay/fail");                             // 인증 실패 시

        // 3) 헤더와 바디 바인딩
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // 4) RestTemplate으로 카카오 서버 호출
        RestTemplate restTemplate = new RestTemplate();

        kakaoReady = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/ready",
                requestEntity,
                KakaoReadyResponse.class
        );

        log.info("카카오페이 Ready 응답: {}", kakaoReady);

        return kakaoReady;
    }

    /**
     * 2. 결제 승인 (Approve API)
     */
    public KakaoApproveResponse kakaoPayApprove(String pgToken, Long orderId) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + adminKey);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("tid", kakaoReady.getTid());                    // Ready 단계에서 받아온 tid
        params.add("partner_order_id", String.valueOf(orderId));
        params.add("partner_user_id", "user123");
        params.add("pg_token", pgToken);                          // 인증 성공 후 주소창으로 전달받은 토큰

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        KakaoApproveResponse approveResponse = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/approve",
                requestEntity,
                KakaoApproveResponse.class
        );

        log.info("카카오페이 Approve 응답: {}", approveResponse);

        return approveResponse;
    }
}

