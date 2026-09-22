package com.dropfit.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("CLOTH")
public class Clothing extends Item {

    private String fabric;
    private String fit;

    public Clothing(String name, int price, String imageUrl, String fabric, String fit) {
        super(name, price, imageUrl);
        this.fabric = fabric;
        this.fit = fit;
    }
}
