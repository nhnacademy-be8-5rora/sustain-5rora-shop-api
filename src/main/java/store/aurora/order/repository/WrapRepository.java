package store.aurora.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.order.entity.Wrap;

public interface WrapRepository extends JpaRepository<Wrap, Long> {
}
