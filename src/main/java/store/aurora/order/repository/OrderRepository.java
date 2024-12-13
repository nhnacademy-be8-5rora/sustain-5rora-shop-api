package store.aurora.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
