package shopping.shop.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemFormDto {

    private Long itemId;
    private String name;
    private int price;
    private int stockQuantity;
    private int count; // <--- 이 필드를 추가하세요!
    private String size;

}
