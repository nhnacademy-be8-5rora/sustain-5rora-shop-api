package store.aurora.cart.exception;

import store.aurora.common.exception.DataNotFoundException;

public class CartItemNotFoundException extends DataNotFoundException {
    public CartItemNotFoundException(String userId, Long bookId) {
        super(String.format(
                "Attempted to delete a non-existent cart item: userId=%s, bookId=%d", userId, bookId
        ));
    }
}