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

    /**
     * 전체 상품과 각 상품의 옵션(ItemOption) 목록을 한 번에 조회 (Fetch Join)
     */
    public List<Item> findItemsWithOption() {
        return em.createQuery(
                        "select distinct i from Item i " +
                                "left join fetch i.options", Item.class)
                .getResultList();
    }

    // 2. Item + Option을 fetch join으로 함께 검색할 때 (공백 무시 검색)
    public List<Item> findItemsWithOptionBySearch(String searchQuery) {
        // 1. 전달받은 검색어의 모든 공백 제거 (null 처리 포함)
        String cleanQuery = (searchQuery != null) ? searchQuery.replaceAll("\\s+", "") : "";

        // 2. JPQL REPLACE 함수를 활용하여 DB의 i.name 내 모든 공백 제거 후 비교
        return em.createQuery(
                        "select distinct i from Item i " +
                                "left join fetch i.options " +
                                "where replace(i.name, ' ', '') like :searchQuery", Item.class)
                .setParameter("searchQuery", "%" + cleanQuery + "%")
                .getResultList();
    }


}
