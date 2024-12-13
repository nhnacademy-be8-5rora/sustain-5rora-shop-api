package store.aurora.cart.service;

import store.aurora.cart.dto.CartItemResponseDTO;
import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;

import java.util.List;

public interface CartService {
    List<CartItemResponseDTO> getCartItemsForLoggedInUser(String userId);
    Cart createCartForUser(String userId);

    void addItemToCart(String userId, Long productId, int quantity);
}
