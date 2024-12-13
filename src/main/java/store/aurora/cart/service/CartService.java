package store.aurora.cart.service;

import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;

import java.util.List;

public interface CartService {
    List<CartItem> getCartItemsForLoggedInUser(String userId);
    Cart createCartForUser(String userId);

    void addItemToCart(String userId, Long productId, int quantity);
}
