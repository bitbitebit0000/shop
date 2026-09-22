package com.dropfit.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.dropfit.domain.item.Item;
import com.dropfit.service.ItemService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;

    @GetMapping("/")
    public String home(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "index";
    }
}
