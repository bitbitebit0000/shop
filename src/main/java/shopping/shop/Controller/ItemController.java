package shopping.shop.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shopping.shop.domain.ItemForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import shopping.shop.domain.ItemFormDto;
import shopping.shop.domain.item.Clothing;
import shopping.shop.domain.item.Item;
import shopping.shop.service.ItemService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/admin/items")
    public String admin(Model model) {
        model.addAttribute("itemForm", new ItemForm());
        return "administrator/admin";
    }

    @GetMapping("/items")
    public String itemList(@RequestParam(value = "searchQuery", required = false) String searchQuery,
                           Model model) {

        List<Item> items;

        // 1. 검색어가 들어왔는지(null이 아니고 공백이 아닌지) 확인
        if (searchQuery != null && !searchQuery.isBlank()) {
            // 검색어가 있으면 LIKE 조건 + Fetch Join 쿼리 실행
            items = itemService.findItemsWithOptionBySearch(searchQuery);
        } else {
            // 검색어가 없으면(그냥 /items 접속) 전체 Fetch Join 쿼리 실행
            items = itemService.findItemsWithOption();
        }

        // 2. 조회 결과를 화면으로 전달
        model.addAttribute("items", items);
        model.addAttribute("searchQuery", searchQuery);

        return "item/items";
    }

    @GetMapping("/createItemForm")
    public String createItemForm(Model model) {
        model.addAttribute("itemForm", new ItemFormDto());
        return "item/createItemForm";
    }








}


