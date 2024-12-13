package store.aurora.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrder(Order order);
}
