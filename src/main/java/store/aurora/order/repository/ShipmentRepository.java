package store.aurora.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.order.entity.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}
