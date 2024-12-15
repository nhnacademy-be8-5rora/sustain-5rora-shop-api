package store.aurora.cart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.cart.dto.CartDTO;
import store.aurora.cart.dto.CartItemResponseDTO;
import store.aurora.cart.entity.CartItem;
import store.aurora.cart.service.CartService;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(@RequestHeader(value = "X-USER-ID", required = false) String userId) {
        if (Objects.isNull(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> result = cartService.getUserCartWithTotalPrice(userId);

        if (((Collection<?>) result.get("cartItems")).isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<String> addItemToCart(@RequestHeader(value = "X-USER-ID", required = false) String userId,
                                                @RequestParam(value = "bookId") Long bookId,
                                                @RequestParam(value = "quantity") int quantity) {
        if (Objects.isNull(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            cartService.addItemToCart(userId, bookId, quantity);
            return ResponseEntity.ok("Item added to cart successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to add item to cart");
        }
    }

    // 로그인하지 않은 사용자의 장바구니 조회 (세션 기반)
    /*

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartForAnonymousUser(HttpSession session) {
        // 세션에서 임시 장바구니 ID를 가져옴
        String sessionCartId = (String) session.getAttribute("cartId");
        List<CartItem> cartItems = cartService.getCartItemsForAnonymousUser(sessionCartId);

        if (cartItems.isEmpty()) {
            return ResponseEntity.noContent().build(); // 장바구니가 비어있으면 204 반환
        }
        return ResponseEntity.ok(cartItems);
    }
     */
}
