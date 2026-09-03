package shopping.shop.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import shopping.shop.domain.ItemForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shopping.shop.domain.item.Charger;
import shopping.shop.domain.item.Item;
import shopping.shop.service.ItemService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("itemForm", new ItemForm());
        return "administrator/admin";
    }

    @PostMapping("/items/new")
    public String createItem(ItemForm itemForm) {
        Item item = new Charger(); // 이걸 어케하지...
        item.setName(itemForm.getName());
        item.setStockQuantity(itemForm.getCount());
        item.setPrice(itemForm.getPrice());
        itemService.saveItem(item);

        return "redirect:/items";
    }

    @GetMapping("/items")
    public String items(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "item/items";
    }

    /*
    @PostMapping("/order")
    public String createOrder(ItemForm itemForm) {
        itemForm.getItemId();
    }

     */

}


