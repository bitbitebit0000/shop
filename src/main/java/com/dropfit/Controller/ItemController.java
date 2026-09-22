package com.dropfit.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.dropfit.domain.item.Item;
import com.dropfit.service.ItemService;

import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("")
    public String itemList(@RequestParam(value = "searchQuery", required = false) String searchQuery,
                           Model model) {

        List<Item> items;

        if (searchQuery != null && !searchQuery.isBlank()) {
            items = itemService.findItemsWithOptionBySearch(searchQuery);
        } else {
            items = itemService.findItemsWithOption();
        }

        model.addAttribute("items", items);
        model.addAttribute("searchQuery", searchQuery);

        return "item/item-list";
    }
}