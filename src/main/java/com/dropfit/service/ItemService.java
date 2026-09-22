package com.dropfit.service;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.dropfit.domain.item.Cap;
import com.dropfit.domain.item.Clothing;
import com.dropfit.domain.item.Item;
import com.dropfit.domain.item.ItemOption;
import com.dropfit.repository.ItemOptionRepository;
import com.dropfit.repository.ItemRepository;

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

    @Transactional
    public void updateItemAndStock(Long itemId, int price, List<Long> optionIds, List<Integer> optionStocks) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found. ID: " + itemId);
        }

        item.changePrice(price);

        if (optionIds != null && optionStocks != null) {
            for (int i = 0; i < optionIds.size(); i++) {
                Long optionId = optionIds.get(i);
                Integer newStock = optionStocks.get(i);

                ItemOption option = itemOptionRepository.findItemOption(optionId);
                if (option == null) {
                    throw new IllegalArgumentException("Item option not found. ID: " + optionId);
                }
                option.changeStock(newStock);
            }
        }
    }

    @Transactional
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId);

        if (item == null) {
            throw new IllegalArgumentException("Item not found. ID: " + itemId);
        }

        itemRepository.delete(item);
    }

    @Transactional
    public Long saveClothing(String name, int price, String imageUrl,
                             String fabric, String fit,
                             List<String> optionNames, List<Integer> optionStocks) {

        Clothing clothing = new Clothing(name, price, imageUrl, fabric, fit);
        addOptionsToItem(clothing, optionNames, optionStocks);

        itemRepository.saveItem(clothing);
        return clothing.getId();
    }

    @Transactional
    public Long saveCap(String name, int price, String imageUrl,
                        String adjustType, String capType,
                        List<String> optionNames, List<Integer> optionStocks) {

        Cap cap = new Cap(name, price, imageUrl, adjustType, capType);
        addOptionsToItem(cap, optionNames, optionStocks);

        itemRepository.saveItem(cap);
        return cap.getId();
    }

    private void addOptionsToItem(Item item, List<String> optionNames, List<Integer> optionStocks) {
        if (optionNames != null && optionStocks != null) {
            for (int i = 0; i < optionNames.size(); i++) {
                String optionName = optionNames.get(i);
                Integer stockQuantity = optionStocks.get(i);

                if (optionName != null && !optionName.isBlank()) {
                    int finalStock = (stockQuantity != null) ? stockQuantity : 0;

                    ItemOption option = new ItemOption(optionName, finalStock);

                    item.addOption(option);
                }
            }
        }
    }
}

