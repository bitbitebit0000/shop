package shopping.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("H")
public class Cap extends Item {

    private String adjustType; // 조절 방식 (예: 버클, 스트랩, 없음)
    private String capType;    // 형태 (예: 볼캡, 버킷햇, 비니)

    public Cap(String name, int price, String imageUrl, String adjustType, String capType) {
        super(name, price, imageUrl);
        this.adjustType = adjustType;
        this.capType = capType;
    }
}

