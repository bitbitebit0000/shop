package shopping.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("E")
public class Earphone extends Item {
    private String connectType; // 연결 방식 (유선, 무선, 커스텀 단자 등)
    private String formFactor;  // 착용 방식 (커널형, 오픈형, 골전도 등)
    private String brand;       // 제조사/브랜드 (애플, 소니, 젠하이저 등)
    private boolean noiseCanceling; // 노이즈 캔슬링 여부 (true/false)

}
