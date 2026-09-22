package com.dropfit.Controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dropfit.domain.item.Item;
import com.dropfit.service.ItemService;

import java.util.List;

@Controller
@RequestMapping("/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemService itemService;

    @GetMapping("")
    public String items(@RequestParam(value = "searchQuery", required = false) String searchQuery,
                        Model model) {

        List<Item> items;

        if (searchQuery != null && !searchQuery.isBlank()) {
            items = itemService.findItemsWithOptionBySearch(searchQuery);
        } else {
            items = itemService.findItemsWithOption();
        }

        model.addAttribute("items", items);
        model.addAttribute("searchQuery", searchQuery);

        return "admin/item-list";
    }

    @PostMapping("/{id}/edit")
    public String updateItem(@PathVariable("id") Long itemId,
                             @RequestParam("price") int price,
                             @RequestParam(value = "optionIds", required = false) List<Long> optionIds,
                             @RequestParam(value = "optionStocks", required = false) List<Integer> optionStocks) {
        itemService.updateItemAndStock(itemId, price, optionIds, optionStocks);
        return "redirect:/admin/items";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable("id") Long itemId) {
            itemService.deleteItem(itemId);
        return "redirect:/admin/items";
    }

    @GetMapping("/new")
    public String createForm() {
        return "admin/create-item";

    }

    @PostMapping("/new")
    public String createItem(
            @RequestParam String itemType,
            @RequestParam String name,
            @RequestParam int price,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String fabric,
            @RequestParam(required = false) String fit,
            @RequestParam(required = false) String adjustType,
            @RequestParam(required = false) String capType,
            @RequestParam(required = false) List<String> optionNames,
            @RequestParam(required = false) List<Integer> optionStocks,
            RedirectAttributes redirectAttributes) {

        if ("CLOTHING".equals(itemType)) {
            itemService.saveClothing(name, price, imageUrl, fabric, fit, optionNames, optionStocks);
        } else if ("CAP".equals(itemType)) {
            itemService.saveCap(name, price, imageUrl, adjustType, capType, optionNames, optionStocks);
        }

        redirectAttributes.addFlashAttribute("message", "Item registered successfully!");

        return "redirect:/admin/items/new";
    }
}
