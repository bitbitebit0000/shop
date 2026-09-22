package com.dropfit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.dropfit.domain.item.Cap;
import com.dropfit.domain.item.Clothing;
import com.dropfit.domain.item.Item;
import com.dropfit.domain.item.ItemOption;

import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class InitService {

    private final ItemService itemService;

    public void dbInit() {
        List<Item> findItems = itemService.findItems();
        if (!findItems.isEmpty()) return;

        String hoodieImg = "https://encrypted-tbn1.gstatic.com/licensed-image?q=tbn:ANd9GcTHhGg0ufAOHx8YeVTTuRbJKxMyfZ2Z2_GC2h-1KY8EHX-p0vFYV1qU5UxdAi3GuZWD5wUnFvGPiPnYE3Y";
        String sneakersImg = "https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=800&q=80";
        String tShirtImg = "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?auto=format&fit=crop&w=800&q=80";
        String capImg = "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?auto=format&fit=crop&w=800&q=80";

        Clothing hoodie = new Clothing(
                "[LIMITED] Heavyweight Overfit Hoodie",
                59000,
                hoodieImg,
                "100% Cotton (700g Heavyweight)",
                "Overfit"
        );
        hoodie.addOption(new ItemOption("S", 5));
        hoodie.addOption(new ItemOption("M", 3));
        hoodie.addOption(new ItemOption("L", 4));
        hoodie.addOption(new ItemOption("XL", 0)); // Out of stock test

        Clothing sneakers = new Clothing(
                "[DROP] Retro High-Top Sneakers",
                149000,
                sneakersImg,
                "Genuine Leather & Rubber Sole",
                "True to Size"
        );
        sneakers.addOption(new ItemOption("250", 2));
        sneakers.addOption(new ItemOption("260", 3));
        sneakers.addOption(new ItemOption("270", 0)); // Out of stock

        Clothing tShirt = new Clothing(
                "Signature Logo Pigment T-Shirt",
                35000,
                tShirtImg,
                "Pigment Washed Cotton",
                "Semi-Overfit"
        );
        tShirt.addOption(new ItemOption("M", 50));
        tShirt.addOption(new ItemOption("L", 50));

        Cap cap = new Cap(
                "Washed Ball Cap - Black",
                29000,
                capImg,
                "Strap Buckle",
                "Ball Cap"
        );
        cap.addOption(new ItemOption("FREE", 50));

        itemService.saveItem(hoodie);
        itemService.saveItem(sneakers);
        itemService.saveItem(tShirt);
        itemService.saveItem(cap);
    }
}