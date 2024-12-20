package store.aurora.cart.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.cart.entity.Cart;
import store.aurora.user.entity.User;
import store.aurora.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfiguration.class)
public class CartRepositoryTest {

    //TODO book entity 양방향으로 바껴서 BookTagList, BookCategoryList 추가 됨

//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private Cart cart;
//    User testUser;
//
//    @BeforeEach
//    void setUp() {
//        testUser = new User("testUser", "hyewon", LocalDate.of(2000, 1, 1), "010-0000-0000", "example@google.com", true);
//        userRepository.save(testUser);
//
//        cart = new Cart(testUser);
//        cartRepository.save(cart);
//    }
//
//    @Test
//    void testFindByUserId() {
//        // 특정 userId로 Cart 조회
//        Optional<Cart> foundCart = cartRepository.findByUserId("testUser");
//
//        // Cart가 존재해야 한다
//        assertThat(foundCart).isPresent();
//        assertThat(foundCart.get().getUser()).isEqualTo(testUser);
//    }
//
//    @Test
//    void testFindByUserId_notFound() {
//        // 존재하지 않는 userId로 조회
//        Optional<Cart> foundCart = cartRepository.findByUserId("nonExistentUser");
//
//        // Cart가 존재하지 않아야 한다
//        assertThat(foundCart).isNotPresent();
//    }
}