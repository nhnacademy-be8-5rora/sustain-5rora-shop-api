package store.aurora.cart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.cart.dto.CartItemDTO;
import store.aurora.cart.service.CartService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(@RequestHeader(value = "X-USER-ID", required = false) String userId,
                                                       HttpServletRequest request) {
        Map<String, Object> result;
        if (Objects.isNull(userId)) {
            result = cartService.getGuestCartWithTotalPrice(request);
        } else {
            result = cartService.getUserCartWithTotalPrice(userId);
        }

        if (((Collection<?>) result.get("cartItems")).isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<String> addItemToCart(@RequestHeader(value = "X-USER-ID", required = false) String userId,
                                                @RequestBody CartItemDTO cartItemDTO,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {

        if (cartItemDTO.getBookId() <= 0 || cartItemDTO.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Invalid bookId or quantity.");
        }

        if (Objects.isNull(userId)) {
            try {
                cartService.addItemToGuestCart(cartItemDTO.getBookId(), cartItemDTO.getQuantity(), request, response);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add item to cart.");
            }
        } else {
            cartService.addItemToCart(userId, cartItemDTO.getBookId(), cartItemDTO.getQuantity());
        }

        return ResponseEntity.ok("Item added to cart successfully");
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteItemToCart(@RequestHeader(value = "X-USER-ID", required = false) String userId,
                                                   @PathVariable("bookId") Long bookId,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {

        if (bookId <= 0) {
            return ResponseEntity.badRequest().body("Invalid bookId.");
        }

        if (Objects.isNull(userId)) {
            cartService.deleteGuestCartItem(bookId, request, response);
        }else {
            cartService.deleteCartItem(userId, bookId);
        }

        return ResponseEntity.ok("Item added to cart successfully");
    }
}
