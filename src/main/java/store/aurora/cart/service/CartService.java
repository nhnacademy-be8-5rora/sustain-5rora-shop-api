package store.aurora.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.aurora.cart.entity.Cart;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface CartService {
    Map<String, Object> getUserCartWithTotalPrice(String userId);
    Cart createUserCart(String userId);

    void addItemToCart(String userId, Long bookId, int quantity);
    void deleteCartItem(String userId, Long bookId);

    /////// 비인증자 ///////
    Map<String, Object> getGuestCartWithTotalPrice(HttpServletRequest request);
    void addItemToGuestCart(Long bookId, int quantity, HttpServletRequest request, HttpServletResponse response) throws IOException;
    void deleteGuestCartItem(Long bookId, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException ;
}
