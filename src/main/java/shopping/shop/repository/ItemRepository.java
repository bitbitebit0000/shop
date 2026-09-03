package shopping.shop.repository;

import shopping.shop.domain.item.Item;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void saveItem(Item item) {
        em.persist(item);
    }

    public Item findById(Long itemId) {
       return em.find(Item.class, itemId);
    }

    public List<Item> findItems() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }



}
