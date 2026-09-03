package shopping.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("K")
public class KeyBoard extends Item {

    private String switchType;   // 스위치 축 종류 (예: 청축, 적축, 갈축, 저소음적축)
    private String connection;   // 연결 방식 (예: 유선, 무선, 블루투스)
    private String layout;       // 배열 (예: 풀배열, 텐키리스, 60% 미니배열)
    private String brand;        // 제조사/브랜드 (예: 로지텍, 앱코, 한성)
}
