package shopping.shop.domain.item;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("C")
public class Clothing extends Item {

    private String fabric; // 원단/소재 (예: 면 100%, 헤비웨이트)
    private String fit;    // 핏 (예: 오버핏, 레귤러핏)

    public Clothing(String name, int price, String imageUrl, String fabric, String fit) {
        super(name, price, imageUrl);
        this.fabric = fabric;
        this.fit = fit;
    }
}
