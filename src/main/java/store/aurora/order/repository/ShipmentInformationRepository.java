package store.aurora.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.order.entity.ShipmentInformation;

public interface ShipmentInformationRepository extends JpaRepository<ShipmentInformation, Long> {
}
