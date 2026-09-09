package shopping.shop.domain;

import shopping.shop.domain.item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shopping.shop.domain.item.ItemOption;

@Entity
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "orderItem_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn
    private ItemOption itemOption;

    private int orderPrice;
    private int count;

    public void cancel() {
       getItemOption().addStock(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

    public static OrderItem createOrderItem(ItemOption itemOption, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemOption(itemOption);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        itemOption.removeStock(count);

        return orderItem;
    }


}
