package shopping.shop.domain.item;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shopping.shop.domain.Order;
import shopping.shop.exception.NotEnoughStockException;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOption {

    @Id @GeneratedValue
    @Column(name = "item_option_id")
    private Long id;

    @ManyToOne
    @JoinColumn
    private Item item;

    private String optionName;
    private String size;
    private int stockQuantity;

    // 생성자는 단순 값 세팅만
    public ItemOption(String size, int stockQuantity) {
        this.size = size;
        this.stockQuantity = stockQuantity;
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        stockQuantity = restStock;
    }
}
