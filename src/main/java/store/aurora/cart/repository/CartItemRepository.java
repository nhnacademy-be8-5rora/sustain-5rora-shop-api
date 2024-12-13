package store.aurora.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartAndBookId(Cart cart, Long bookId);  // 장바구니 ID와 상품 ID로 조회

}
