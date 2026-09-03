package shopping.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("C")
public class Charger extends Item {

    private String portType;     // 포트 타입 (예: C타입, A타입, 3in1 무선)
    private int outputPower;     // 출력(W) (예: 25, 65, 100)
    private String brand;       // 제조사/브랜드 (예: 삼성, Anker, 벨킨)
    private boolean fastCharging; // 고속 충전 지원 여부 (true/false)
}

