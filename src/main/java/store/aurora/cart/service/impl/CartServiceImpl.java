package store.aurora.cart.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;
import store.aurora.cart.repository.CartItemRepository;
import store.aurora.cart.repository.CartRepository;
import store.aurora.cart.service.CartService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public List<CartItem> getCartItemsForLoggedInUser(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));

        return cartItemRepository.findByCartId(cart.getId());
    }

    // 로그인하지 않은 사용자의 장바구니 조회 (세션 또는 쿠키 사용)
    public List<CartItem> getCartItemsForAnonymousUser(String sessionCartId) {
        // 세션 또는 쿠키에서 장바구니 ID를 가져와서 조회
        if (sessionCartId != null) {
            Cart cart = cartRepository.findById(Long.parseLong(sessionCartId)).orElse(null);
            if (cart != null) {
                return cartItemRepository.findByCartId(cart.getId());
            }
        }
        return List.of(); // 빈 리스트 반환
    }

    @Override
    @Transactional
    public void addItemToCart(String userId, Long bookId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));

        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartAndBookId(cart, bookId);
        if(cartItemOptional.isEmpty()) {
            createCartItem(cart, bookId, quantity); // todo 북 조회
        }else {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(quantity);
        }
    }

    @Override
    @Transactional
    public Cart createCartForUser(String userId) {
        Cart cart = new Cart(userId);
        return cartRepository.save(cart);
    }

    private void createCartItem(Cart cart, Long bookId, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setBookId(bookId);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
}
