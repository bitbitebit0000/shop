package com.dropfit.domain.item;

import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private int price;
    private String imageUrl;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemOption> options = new ArrayList<>();

    public void addOption(ItemOption option) {
        options.add(option);
        option.setItem(this);
    }

    public int getTotalStockQuantity() {
        int total = 0;
        for (ItemOption itemOption : options) {
            total += itemOption.getStockQuantity();
        }
        return total;
    }

    public void changePrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price must be greater than or equal to 0.");
        }
        this.price = price;
    }

    public Item(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}