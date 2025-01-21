package store.aurora.cart.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import store.aurora.book.dto.BookInfoDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.service.book.BookService;
import store.aurora.cart.dto.CachedCart;
import store.aurora.cart.dto.CartDTO;
import store.aurora.cart.dto.CartItemDTO;
import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;
import store.aurora.cart.exception.CartItemNotFoundException;
import store.aurora.cart.repository.CartItemRepository;
import store.aurora.cart.repository.CartRepository;
import store.aurora.user.entity.User;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserCartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookService bookService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    String userId = "user123";
    String cacheKey = "cart:" + userId;
    User testUser = new User(userId, "hyewon", LocalDate.of(2000, 1, 1), "010-0000-0000", "example@google.com", true);
    Cart cart = new Cart(testUser);


    Long bookId = 1L;
    Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(bookId);
    }

    @Test
    void testGetUserCartWithTotalPrice_CacheHit() {
        // Arrange
        CachedCart cachedCart = new CachedCart(
                List.of(new CartItemDTO(1L, 2))
        );

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(cachedCart);

        BookInfoDTO bookInfo = new BookInfoDTO();
        bookInfo.setSalePrice(100);
        bookInfo.setRegularPrice(120);
        bookInfo.setTitle("Test Book");
        bookInfo.setStock(10);
        bookInfo.setSale(true);

        when(bookService.getBookInfo(List.of(1L))).thenReturn(List.of(bookInfo));

        // Act
        Map<String, Object> result = cartService.getUserCartWithTotalPrice(userId);

        // Assert
        assertThat(result)
                .isNotNull()
                .containsEntry("totalPrice", 200);
        List<CartDTO> cartItems = (List<CartDTO>) result.get("cartItems");
        assertThat(cartItems).hasSize(1);
        assertThat(cartItems.getFirst().getTitle()).isEqualTo("Test Book");
    }

    @Test
    void getUserCartWithTotalPrice_shouldFetchFromDatabase_whenCacheDoesNotExist() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(null);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // When
        var result = cartService.getUserCartWithTotalPrice(userId);

        // Then
        verify(valueOperations, times(1)).set(eq(cacheKey), any(CachedCart.class));
        assertThat(result).isNotNull();
    }

    @Test
    void testAddItemToCart_NewItem() {
        // Arrange
        int quantity = 3;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(cartItemRepository.findByCartAndBookId(cart, bookId)).thenReturn(Optional.empty());
        when(bookService.getBookById(bookId)).thenReturn(book);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // Act
        cartService.addItemToCart(userId, bookId, quantity);

        // Assert
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(valueOperations, times(1)).set(eq(cacheKey), any(CachedCart.class));
    }

    @Test
    void testAddItemToCart_UpdateQuantity() {
        // Arrange
        int quantity = 5;

        CartItem existingCartItem = new CartItem(cart, book);
        existingCartItem.setQuantity(2);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(cartItemRepository.findByCartAndBookId(cart, bookId)).thenReturn(Optional.of(existingCartItem));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // Act
        cartService.addItemToCart(userId, bookId, quantity);

        // Assert
        assertThat(existingCartItem.getQuantity()).isEqualTo(quantity);
        verify(valueOperations, times(1)).set(eq(cacheKey), any(CachedCart.class));
    }

    @Test
    void testDeleteCartItem_ItemExists() {
        // Arrange
        CartItem cartItem = new CartItem(cart, book);
        cart.getCartItems().add(cartItem);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // Act
        cartService.deleteCartItem(userId, bookId);

        // Assert
        assertThat(cart.getCartItems()).isEmpty();
        verify(valueOperations, times(1)).set(eq(cacheKey), any(CachedCart.class));
    }

    @Test
    void testDeleteCartItem_ItemDoesNotExist() {
        // Arrange
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(CartItemNotFoundException.class, () ->
                cartService.deleteCartItem(userId, bookId)
        );
    }
}