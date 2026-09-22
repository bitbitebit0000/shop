package com.dropfit.repository;

import com.dropfit.domain.item.Item;
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

    public List<Item> findItemsWithOption() {
        return em.createQuery(
                        "select distinct i from Item i " +
                                "left join fetch i.options", Item.class)
                .getResultList();
    }

    public List<Item> findItemsWithOptionBySearch(String searchQuery) {
        String cleanQuery = (searchQuery != null) ? searchQuery.replaceAll("\\s+", "") : "";

        return em.createQuery(
                        "select distinct i from Item i " +
                                "left join fetch i.options " +
                                "where replace(i.name, ' ', '') like :searchQuery", Item.class)
                .setParameter("searchQuery", "%" + cleanQuery + "%")
                .getResultList();
    }

    public void delete(Item item) {
        em.remove(item);
    }
}
