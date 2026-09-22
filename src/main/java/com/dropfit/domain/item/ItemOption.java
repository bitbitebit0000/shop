package com.dropfit.domain.item;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.dropfit.exception.NotEnoughStockException;

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

    public ItemOption(String size, int stockQuantity) {
        this.size = size;
        this.stockQuantity = stockQuantity;
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("Not enough stock.");
        }
        stockQuantity = restStock;
    }

    public void changeStock(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity must be greater than or equal to 0.");
        }
        this.stockQuantity = stockQuantity;
    }
}