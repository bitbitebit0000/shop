package shopping.shop.service;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import shopping.shop.domain.item.Item;
import shopping.shop.domain.item.ItemOption;
import shopping.shop.repository.ItemOptionRepository;
import shopping.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.saveItem(item);
    }

    public Item findItem(Long itemId) {
        return itemRepository.findById(itemId);
    }

    public List<Item> findItems() {
        return itemRepository.findItems();
    }


    public List<Item> findItemsWithOption() {
        return itemRepository.findItemsWithOption();
    }


    public List<Item> findItemsWithOptionBySearch(String searchQuery) {
        return itemRepository.findItemsWithOptionBySearch(searchQuery);
    }


    public ItemOption findItemOption(Long optionId) {
        return itemOptionRepository.findItemOption(optionId);
    }


}
