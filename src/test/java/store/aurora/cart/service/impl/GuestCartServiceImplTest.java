package store.aurora.cart.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.book.dto.BookInfoDTO;
import store.aurora.book.service.BookService;
import store.aurora.cart.dto.CartDTO;
import store.aurora.cart.dto.CartItemDTO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class GuestCartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private BookService bookService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ObjectMapper objectMapper;

    Long bookId = 1L;
    int quantity = 2;
    private static final String CART_COOKIE_NAME = "CART";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddItemToGuestCart_NewItem() throws UnsupportedEncodingException, JsonProcessingException {
        // Arrange
        when(request.getCookies()).thenReturn(null);
        doNothing().when(bookService).notExistThrow(bookId);

        // Act
        cartService.addItemToGuestCart(bookId, quantity, request, response);

        // Assert
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testAddItemToGuestCart_UpdateQuantity() throws UnsupportedEncodingException, JsonProcessingException {
        // Arrange
        List<CartItemDTO> existingCart = new ArrayList<>();
        existingCart.add(new CartItemDTO(bookId, 1));

        Cookie cookie = new Cookie(CART_COOKIE_NAME, URLEncoder.encode(objectMapper.writeValueAsString(existingCart), StandardCharsets.UTF_8));
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // Act
        cartService.addItemToGuestCart(bookId, quantity, request, response);

        // Assert
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testDeleteGuestCartItem() throws UnsupportedEncodingException, JsonProcessingException {
        // Arrange
        List<CartItemDTO> existingCart = new ArrayList<>();
        existingCart.add(new CartItemDTO(bookId, 1));

        Cookie cookie = new Cookie(CART_COOKIE_NAME, URLEncoder.encode(objectMapper.writeValueAsString(existingCart), StandardCharsets.UTF_8));
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // Act
        cartService.deleteGuestCartItem(bookId, request, response);

        // Assert
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testGetGuestCartWithTotalPrice() throws JsonProcessingException {
        // Arrange
        List<CartItemDTO> existingCart = new ArrayList<>();
        existingCart.add(new CartItemDTO(bookId, quantity));

        Cookie cookie = new Cookie(CART_COOKIE_NAME, URLEncoder.encode(objectMapper.writeValueAsString(existingCart), StandardCharsets.UTF_8));
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        BookInfoDTO bookInfo = new BookInfoDTO();
        bookInfo.setTitle("Test Book");
        bookInfo.setSalePrice(100);
        bookInfo.setRegularPrice(120);
        bookInfo.setStock(10);
        bookInfo.setSale(true);

        when(bookService.getBookInfo(List.of(bookId))).thenReturn(List.of(bookInfo));

        // Act
        Map<String, Object> result = cartService.getGuestCartWithTotalPrice(request);

        // Assert
        assertThat(result)
                .isNotNull()
                .containsEntry("totalPrice", 200);
        List<CartDTO> cartItems = (List<CartDTO>) result.get("cartItems");
        assertThat(cartItems).hasSize(1);
        assertThat(cartItems.getFirst().getTitle()).isEqualTo("Test Book");
    }
}