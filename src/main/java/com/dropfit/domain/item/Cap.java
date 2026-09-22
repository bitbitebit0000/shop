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
@DiscriminatorValue("CAP")
public class Cap extends Item {

    private String adjustType;
    private String capType;

    public Cap(String name, int price, String imageUrl, String adjustType, String capType) {
        super(name, price, imageUrl);
        this.adjustType = adjustType;
        this.capType = capType;
    }
}

