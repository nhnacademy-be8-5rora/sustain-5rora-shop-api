package store.aurora.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrder(Order order);
    boolean existsByOrderUserIdAndBookId(String userId, Long bookId);

    @Query("SELECT SUM(w.amount * od.quantity) " +
            "FROM OrderDetail od " +
            "JOIN od.wrap w " + // INNER JOIN으로 Wrap이 없는 경우 제외
            "WHERE od.order.id = :orderId")
    Integer calculateTotalWrapCostByOrderId(Long orderId);
}