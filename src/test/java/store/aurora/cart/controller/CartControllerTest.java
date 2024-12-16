package store.aurora.cart.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import store.aurora.cart.dto.CartDTO;
import store.aurora.cart.service.CartService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

@WebMvcTest(CartController.class)
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    private static final String USER_ID = "testUser";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService)).build();
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
        // given
        doNothing().when(cartService).addItemToCart(anyString(), anyLong(), anyInt());

        // when & then
        mockMvc.perform(post("/api/cart")
                        .header("X-USER-ID", USER_ID)
                        .param("bookId", "1")
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Item added to cart successfully"));

        verify(cartService, times(1)).addItemToCart(USER_ID, 1L, 3);
    }

    @Test
    void testAddItemToCart_invalidBookId() throws Exception {
        // when & then
        mockMvc.perform(post("/api/cart")
                        .header("X-USER-ID", USER_ID)
                        .param("bookId", "0")
                        .param("quantity", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid bookId or quantity."));
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
}