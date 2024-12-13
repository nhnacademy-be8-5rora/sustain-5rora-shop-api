package store.aurora.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.cart.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
