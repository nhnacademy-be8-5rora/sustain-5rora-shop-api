package store.aurora.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.exception.exception404.OrderNotFoundException;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.service.impl.OrderServiceImpl;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserStatus;
import store.aurora.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = new OrderServiceImpl(orderRepository);

        userRepository = Mockito.mock(UserRepository.class);
    }

    @Test
    void isExist() {
        when(orderRepository.existsById(anyLong())).thenReturn(true);
        assertTrue(orderService.isExist(1L));
    }

    @Test
    void isExistWithNonExistOrderId() {
        when(orderRepository.existsById(anyLong())).thenReturn(false);
        assertFalse(orderService.isExist(1L));
    }

    @Test
    void isExistWithNullOrderId() {
        assertThrows(IllegalArgumentException.class, () -> orderService.isExist(null));
    }

    @Test
    void createOrder() {
        User mockUser = new User();
        mockUser.setId("john_doe");
        mockUser.setPassword("secure_password");
        mockUser.setName("John Doe");
        mockUser.setBirth(LocalDate.of(1990, 1, 1)); // 생년월일
        mockUser.setPhoneNumber("010-1234-5678");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setStatus(UserStatus.ACTIVE); // enum 값 설정
        mockUser.setLastLogin(LocalDateTime.now());
        mockUser.setSignUpDate(LocalDate.now());
        mockUser.setIsOauth(false);

        when(userRepository.getReferenceById(anyString())).thenReturn(mockUser);

        Order order = Order.builder()
                .deliveryFee(0)
                .orderTime(LocalDateTime.now())
                .totalAmount(0)
                .pointAmount(0)
                .state(OrderState.PENDING)
                .name("John Doe")
                .orderPhone("010-1234-5678")
                .user(mockUser)
                .build();

        Order nonUserOrder = Order.builder()
                .deliveryFee(0)
                .orderTime(LocalDateTime.now())
                .totalAmount(0)
                .pointAmount(0)
                .state(OrderState.PENDING)
                .name("John Doe")
                .orderPhone("010-1234-5678")
                .password("non_user_password")
                .build();

        orderService.createOrder(order);
        orderService.createOrder(nonUserOrder);

        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void createOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(null));
    }

    @Test
    void createOrderWithNullColumns() {
        assertAll(
                () -> {
                    Order o = Order.builder()
                            .deliveryFee(null)
                            .orderTime(LocalDateTime.now())
                            .totalAmount(0)
                            .pointAmount(0)
                            .state(OrderState.PENDING)
                            .name("John Doe")
                            .orderPhone("010-1234-5678")
                            .build();
                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                () -> {
                    Order o = Order.builder()
                            .deliveryFee(0)
                            .orderTime(null)
                            .totalAmount(0)
                            .pointAmount(0)
                            .state(OrderState.PENDING)
                            .name("John Doe")
                            .orderPhone("010-1234-5678")
                            .build();
                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                () -> {
                    Order o = Order.builder()
                            .deliveryFee(0)
                            .orderTime(LocalDateTime.now())
                            .totalAmount(null)
                            .pointAmount(0)
                            .state(OrderState.PENDING)
                            .name("John Doe")
                            .orderPhone("010-1234-5678")
                            .build();
                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                () -> {
                    Order o = Order.builder()
                            .deliveryFee(0)
                            .orderTime(LocalDateTime.now())
                            .totalAmount(0)
                            .pointAmount(null)
                            .state(OrderState.PENDING)
                            .name("John Doe")
                            .orderPhone("010-1234-5678")
                            .build();
                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                () -> {
                    Order o = Order.builder()
                            .deliveryFee(0)
                            .orderTime(LocalDateTime.now())
                            .totalAmount(0)
                            .pointAmount(0)
                            .state(null)
                            .name("John Doe")
                            .orderPhone("010-1234-5678")
                            .build();
                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                () -> {
                    Order o = Order.builder()
                            .deliveryFee(0)
                            .orderTime(LocalDateTime.now())
                            .totalAmount(0)
                            .pointAmount(0)
                            .state(OrderState.PENDING)
                            .name(null)
                            .orderPhone("010-1234-5678")
                            .build();
                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                () -> {
                    Order o = Order.builder()
                            .deliveryFee(0)
                            .orderTime(LocalDateTime.now())
                            .totalAmount(0)
                            .pointAmount(0)
                            .state(OrderState.PENDING)
                            .name("John Doe")
                            .orderPhone(null)
                            .build();
                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                }
        );
    }

    @Test
    void getOrder() {
        when(orderRepository.getReferenceById(anyLong())).thenReturn(new Order());
        when(orderRepository.existsById(anyLong())).thenReturn(true);

        orderService.getOrder(1L);

        verify(orderRepository, times(1)).getReferenceById(anyLong());
    }

    @Test
    void getOrderWithNullOrderId() {
        assertThrows(IllegalArgumentException.class, () -> orderService.getOrder(null));
    }

    @Test
    void getOrderWithNonExistOrderId() {
        when(orderRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(1L));
    }

    @Test
    void getOrders() {
        orderService.getOrders();
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void updateOrder() {
        Order order = Order.builder()
                .id(1L)
                .deliveryFee(0)
                .orderTime(LocalDateTime.now())
                .totalAmount(0)
                .pointAmount(0)
                .state(OrderState.PENDING)
                .name("John Doe")
                .orderPhone("010-1234-5678")
                .password("")
                .build();

        when(orderRepository.existsById(anyLong())).thenReturn(true);

        orderService.updateOrder(order);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> orderService.updateOrder(null));
    }

    @Test
    void updateOrderWithNotExist() {
        Order order = Order.builder()
                .id(1L)
                .deliveryFee(0)
                .orderTime(LocalDateTime.now())
                .totalAmount(0)
                .pointAmount(0)
                .state(OrderState.PENDING)
                .name("John Doe")
                .orderPhone("010-1234-5678")
                .password("")
                .build();

        when(orderRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(order));
    }

    @Test
    void deleteOrderById() {
        when(orderRepository.existsById(anyLong())).thenReturn(true);

        orderService.deleteOrderById(1L);

        verify(orderRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteOrderByIdWithNonExistOrderId() {
        when(orderRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrderById(1L));
    }
}