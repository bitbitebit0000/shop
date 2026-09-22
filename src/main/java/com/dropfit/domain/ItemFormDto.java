package com.dropfit.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemFormDto {

    private Long itemId;
    private String name;
    private int price;
    private int stockQuantity;
    private int count;
    private String size;
}
