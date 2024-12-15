package store.aurora.cart.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Book;
import store.aurora.book.service.BookService;
import store.aurora.cart.dto.CartDTO;
import store.aurora.cart.dto.CartItemResponseDTO;
import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;
import store.aurora.cart.repository.CartItemRepository;
import store.aurora.cart.repository.CartRepository;
import store.aurora.cart.service.CartService;
import store.aurora.user.service.UserService;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final UserService userService;
    private final BookService bookService;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, UserService userService, BookService bookService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    private Cart getUserCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createUserCart(userId));
    }

    @Override
    @Transactional
    public Map<String, Object> getUserCartWithTotalPrice(String userId) {
        Cart cart = getUserCart(userId);

        List<CartDTO> cartItems = cartItemRepository.findByCartId(cart.getId()).stream()
                .map(cartItem -> new CartDTO(
                        cartItem.getBook().getId(),
                        cartItem.getQuantity()
                )).toList();

        /* todo
        List<Long> bookIds = cartItems.stream()
                .map(CartDTO::getBookId)
                .toList();

        List<BookInfoDTO> bookInfoList = bookService.getBookInfo(bookIds);

        for (int i = 0; i < cartItems.size(); i++) {
            CartDTO cartItem = cartItems.get(i);
            BookInfoDTO bookInfo = bookInfoList.get(i);
            cartItem.setTitle(bookInfo.getTitle());
            cartItem.setPrice(bookInfo.getPrice());
        }

        double totalPrice = cartItems.stream()
                .mapToDouble(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                .sum();
         */

        Map<String, Object> result = new HashMap<>();
        result.put("cartItems", cartItems);
//        result.put("totalPrice", totalPrice);
        return result;
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
        Cart cart = getUserCart(userId);

        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartAndBookId(cart, bookId);
        if(cartItemOptional.isEmpty()) {
            createCartItem(cart, bookId, quantity);
        }else {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(quantity);
        }
    }

    @Override
    @Transactional
    public void deleteCartItem(String userId, Long bookId) {
        Cart cart = getUserCart(userId);
        cart.getCartItems().removeIf(cartItem -> Objects.equals(cartItem.getId(), bookId));
    }

    @Override
    public Cart createUserCart(String userId) {
        Cart cart = new Cart(userService.getUser(userId));
        return cartRepository.save(cart);
    }

    private void createCartItem(Cart cart, Long bookId, int quantity) {
        Book book = bookService.getBookById(bookId);
        CartItem cartItem = new CartItem(cart, book);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
}
