package store.aurora.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrder(Order order);
}
