package shopping.shop.repository;

import shopping.shop.domain.Order;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOrder(Long orderId) {
        return em.find(Order.class, orderId);
    }

    public List<Order> findOrders() {
        return em.createQuery("select o from Order o", Order.class)
                .getResultList();
    }

    public List<Order> findAll(Long memberId) {
        return em.createQuery(
                        "select distinct o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d" +
                                " join fetch o.orderItems oi" +
                                " join fetch oi.item i" +
                                " where m.id = :memberId" +
                                " order by o.orderDate desc", Order.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}