package store.aurora.cart.service;

import store.aurora.cart.dto.CartDTO;
import store.aurora.cart.dto.CartItemResponseDTO;
import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;

import java.util.List;
import java.util.Map;

public interface CartService {
    Map<String, Object> getUserCartWithTotalPrice(String userId);
    Cart createUserCart(String userId);

    void addItemToCart(String userId, Long productId, int quantity);
}
