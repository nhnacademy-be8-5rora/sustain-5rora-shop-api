package store.aurora.cart.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Publisher;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.PublisherRepository;
import store.aurora.cart.entity.Cart;
import store.aurora.cart.entity.CartItem;
import store.aurora.user.entity.User;
import store.aurora.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    private Cart cart;
    private Book book;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        User testUser = new User("testId", "hyewon", LocalDate.of(2000, 1, 1), "010-0000-0000", "example@google.com", true);
        userRepository.save(testUser);

        cart = new Cart(testUser);
        cartRepository.save(cart);

        Publisher publisher = new Publisher();
        publisher.setName("publisher");
        publisherRepository.save(publisher);

        book = new Book();
        book.setTitle("Test Book");
        book.setRegularPrice(1000);
        book.setSalePrice(800);
        book.setStock(50);
        book.setSale(true);
        book.setIsbn("book-isbn");
        book.setContents("Test Contents");
        book.setPublishDate(LocalDate.now());
        book.setPublisher(publisher);
        book.setExplanation("Test Explanation");
        bookRepository.save(book);

        cartItem = new CartItem(cart, book);
        cartItemRepository.save(cartItem);
    }

    @Test
    void testFindByCartId() {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        assertThat(cartItems).hasSize(1);
        assertThat(cartItems.get(0).getCart()).isEqualTo(cart);
        assertThat(cartItems.get(0).getBook()).isEqualTo(book);
    }

    @Test
    void testFindByCartAndBookId() {
        Optional<CartItem> foundCartItem = cartItemRepository.findByCartAndBookId(cart, book.getId());

        assertThat(foundCartItem).isPresent();
        assertThat(foundCartItem.get().getCart()).isEqualTo(cart);
        assertThat(foundCartItem.get().getBook()).isEqualTo(book);
    }


    @Test
    void testFindByCartAndBookId_notFound() {
        Book nonExistentBook = new Book();
        nonExistentBook.setId(999L);
        Optional<CartItem> foundCartItem = cartItemRepository.findByCartAndBookId(cart, nonExistentBook.getId());

        assertThat(foundCartItem).isNotPresent();
    }
}