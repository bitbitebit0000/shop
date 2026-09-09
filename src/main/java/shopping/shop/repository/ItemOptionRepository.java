package shopping.shop.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shopping.shop.domain.item.Item;
import shopping.shop.domain.item.ItemOption;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemOptionRepository {

    private final EntityManager em;

    public ItemOption findItemOption(Long itemOptionId) {
        return em.find(ItemOption.class, itemOptionId);
    }

 }

