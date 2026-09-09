package shopping.shop.domain.item;

import lombok.*;
import shopping.shop.domain.OrderItem;
import shopping.shop.exception.NotEnoughStockException;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {

    @Id @GeneratedValue //identity 전략을 써야하나
    @Column(name = "item_id")
    private Long id;
    @Column(nullable = false)
    private String name;
    private int price;
    //private int stockQuantity;
    private String imageUrl;

    @OneToMany(mappedBy = "item",cascade = CascadeType.ALL)
    private List<ItemOption> options = new ArrayList<>();

    public void addOption(ItemOption option) {
        options.add(option);
        option.setItem(this);
    }

    public int getTotalStockQuantity() {
        int total = 0;
       for(ItemOption itemOption : options) {
           total += itemOption.getStockQuantity();
       }
       return total;
    }

    public Item(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

}
