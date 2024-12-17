package store.aurora.cart.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.BookInfoDTO;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Publisher;
import store.aurora.book.service.BookService;
import store.aurora.cart.dto.CartDTO;
import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;
import store.aurora.cart.repository.CartItemRepository;
import store.aurora.cart.repository.CartRepository;
import store.aurora.user.entity.User;
import store.aurora.user.service.UserService;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceImplTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartItem cartItem;
    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 초기화

        User testUser = new User("testId", "hyewon", LocalDate.of(2000, 1, 1), "010-0000-0000", "example@google.com", true);
        cart = new Cart(testUser);

        Publisher publisher = new Publisher();
        publisher.setName("publisher");

        book = new Book();
        book.setTitle("Test Book");
        book.setRegularPrice(1000);
        book.setSalePrice(800);
        book.setStock(50);
        book.setSale(true);
        book.setPublishDate(LocalDate.now());
        book.setPublisher(publisher);
        book.setExplanation("Test Explanation");

        cartItem = new CartItem(cart, book);

        when(cartRepository.findByUserId("testUser")).thenReturn(Optional.of(cart));
    }

    @Test
    void testGetUserCartWithTotalPrice() {
        // given
        List<CartItem> cartItems = Collections.singletonList(cartItem);
        when(cartItemRepository.findByCartId(any())).thenReturn(cartItems);

        List<BookInfoDTO> bookInfoDTOList = new ArrayList<>();
        BookInfoDTO bookInfoDTO = new BookInfoDTO();
        bookInfoDTO.setTitle(book.getTitle());
        bookInfoDTO.setRegularPrice(book.getRegularPrice());
        bookInfoDTO.setSalePrice(book.getSalePrice());
        bookInfoDTO.setStock(book.getStock());
        bookInfoDTO.setSale(book.isSale());
        bookInfoDTO.setFilePath("path/to/image.jpg");
        bookInfoDTOList.add(bookInfoDTO);
        when(bookService.getBookInfo(Collections.singletonList(book.getId()))).thenReturn(bookInfoDTOList);

        // when
        Map<String, Object> result = cartService.getUserCartWithTotalPrice("testUser");

        // then
        assertThat(result).containsKey("cartItems");
        assertThat(result).containsKey("totalPrice");
        assertThat(result.get("cartItems")).isInstanceOf(List.class);
        assertThat(((List<?>) result.get("cartItems")).get(0)).isInstanceOf(CartDTO.class);

        List<CartDTO> cartItemsList = (List<CartDTO>) result.get("cartItems");
        assertThat(cartItemsList.get(0).getBookId()).isEqualTo(book.getId());
        assertThat(result.get("totalPrice")).isEqualTo(800);
    }

    @Test
    @Transactional
    void testAddItemToCart_NewItem() {
        // given
        when(cartItemRepository.findByCartAndBookId(cart, book.getId())).thenReturn(Optional.empty());

        // when
        cartService.addItemToCart("testUser", book.getId(), 3);

        // then
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @Transactional
    void testAddItemToCart_UpdateItem() {
        // given
        when(cartItemRepository.findByCartAndBookId(cart, book.getId())).thenReturn(Optional.of(cartItem));

        // when
        cartService.addItemToCart("testUser", book.getId(), 5);

        // then
        verify(cartItemRepository, times(0)).save(cartItem);
        assertThat(cartItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @Transactional
    void testDeleteCartItem() {
        // given
        cart.getCartItems().add(cartItem);
        when(cartItemRepository.findByCartAndBookId(cart, book.getId())).thenReturn(Optional.of(cartItem));

        // when
        cartService.deleteCartItem("testUser", book.getId());

        // then
        assertThat(cart.getCartItems()).isEmpty();
    }
}