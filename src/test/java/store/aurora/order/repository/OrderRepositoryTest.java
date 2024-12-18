package store.aurora.order.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserStatus;
import store.aurora.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(QuerydslConfiguration.class)
@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUser(){
        User user = new User();
        user.setId("test");
        user.setName("test");
        user.setBirth(LocalDate.now());
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test@test.com");
        user.setStatus(UserStatus.ACTIVE);
        user.setSignUpDate(LocalDate.now());
        user.setIsOauth(false);
        userRepository.save(user);

        Order order = new Order();
        order.setDeliveryFee(1000);
        order.setOrderTime(LocalDateTime.now());
        order.setTotalAmount(50000);
        order.setPointAmount(0);
        order.setState(OrderState.CONFIRMED);
        order.setName("John Doe");
        order.setOrderPhone("010-1234-5678");
        order.setOrderEmail("johndoe@example.com");
        order.setUser(user);
        orderRepository.save(order);

        Order order1 = new Order();
        order1.setDeliveryFee(1000);
        order1.setOrderTime(LocalDateTime.now());
        order1.setTotalAmount(50000);
        order1.setPointAmount(0);
        order1.setState(OrderState.CONFIRMED);
        order1.setName("John Doe");
        order1.setOrderPhone("010-1234-5678");
        order1.setOrderEmail("johndoe@example.com");
        order1.setUser(user);
        orderRepository.save(order1);

        List<Order> orders = orderRepository.findByUser(user);
        assertEquals(2, orders.size());

        Order retrievedOrder = orders.getFirst();
        assertNotNull(retrievedOrder);
        assertEquals(order.getId(), retrievedOrder.getId());
    }
}