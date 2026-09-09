package shopping.shop.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import shopping.shop.domain.item.Item;
import shopping.shop.service.ItemService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;

    @GetMapping("/")
    public String home(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "home";
    }



}
