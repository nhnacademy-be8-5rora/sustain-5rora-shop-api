package store.aurora.cart.controller;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.cart.dto.CartDTO;
import store.aurora.cart.dto.CartItemDTO;
import store.aurora.cart.service.CartService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(CartController.class)
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    private static final String USER_ID = "testUser";

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetCart_withItems() throws Exception {
        // given
        Map<String, Object> result = Map.of(
                "cartItems", List.of(new CartDTO(1L, 2)),
                "totalPrice", 5000
        );
        when(cartService.getUserCartWithTotalPrice(USER_ID)).thenReturn(result);

        // when & then
        mockMvc.perform(get("/api/cart")
                        .header("X-USER-ID", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems").isArray())
                .andExpect(jsonPath("$.cartItems.length()").value(1))
                .andExpect(jsonPath("$.totalPrice").value(5000));
    }

    @Test
    void testGetCart_noItems() throws Exception {
        // given
        Map<String, Object> result = Map.of("cartItems", List.of(), "totalPrice", 0);
        when(cartService.getUserCartWithTotalPrice(USER_ID)).thenReturn(result);

        // when & then
        mockMvc.perform(get("/api/cart")
                        .header("X-USER-ID", USER_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAddItemToCart_valid() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(1L, 2);

        // Act & Assert
        mockMvc.perform(post("/api/cart")
                        .header("X-USER-ID", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk());

        verify(cartService, times(1)).addItemToCart(USER_ID, cartItemDTO.getBookId(), cartItemDTO.getQuantity());
    }

    @Test
    void testAddItemToCart_invalidBookId() throws Exception {
        // Arrange
        CartItemDTO cartItemDTO = new CartItemDTO(0L, -1);

        // Act & Assert
        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    @Test
    void testDeleteItemToCart_valid() throws Exception {
        // given
        doNothing().when(cartService).deleteCartItem(anyString(), anyLong());

        // when & then
        mockMvc.perform(delete("/api/cart/{bookId}", 1)
                        .header("X-USER-ID", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("Item added to cart successfully"));

        verify(cartService, times(1)).deleteCartItem(USER_ID, 1L);
    }

    @Test
    void testDeleteItemToCart_invalidBookId() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/cart/{bookId}", 0)
                        .header("X-USER-ID", USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid bookId."));
    }

    @Test
    void testGetCart_AsGuest() throws Exception {
        // Arrange
        Map<String, Object> cartResponse = new HashMap<>();
        cartResponse.put("cartItems", List.of(new CartDTO(1L, 2, "Test Book", 100, 80, 10, true, "path/to/image")));
        cartResponse.put("totalPrice", 160);

        when(cartService.getGuestCartWithTotalPrice(any(HttpServletRequest.class))).thenReturn(cartResponse);

        // Act & Assert
        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems").isArray())
                .andExpect(jsonPath("$.cartItems[0].bookId").value(1))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(160));

        verify(cartService, times(1)).getGuestCartWithTotalPrice(any(HttpServletRequest.class));
    }

    @Test
    void testGetCart_AsGuest_NoItems() throws Exception {
        // Arrange
        Map<String, Object> cartResponse = new HashMap<>();
        cartResponse.put("cartItems", Collections.emptyList());
        cartResponse.put("totalPrice", 0);

        when(cartService.getGuestCartWithTotalPrice(any(HttpServletRequest.class))).thenReturn(cartResponse);

        // Act & Assert
        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).getGuestCartWithTotalPrice(any(HttpServletRequest.class));
    }

    @Test
    void testAddItemToCart_AsGuest() throws Exception {
        // Arrange
        CartItemDTO cartItemDTO = new CartItemDTO(1L, 2);

        // Act & Assert
        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk());

        verify(cartService, times(1)).addItemToGuestCart(eq(cartItemDTO.getBookId()), eq(cartItemDTO.getQuantity()), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void testDeleteItemToCart_AsGuest() throws Exception {
        // Arrange
        Long bookId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/cart/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cartService, times(1)).deleteGuestCartItem(eq(bookId), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}