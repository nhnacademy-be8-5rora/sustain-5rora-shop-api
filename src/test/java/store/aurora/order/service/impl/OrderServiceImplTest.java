package store.aurora.order.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.mapper.OrderMapper;
import store.aurora.order.repository.OrderRepository;
import store.aurora.user.entity.User;
import store.aurora.user.entity.UserStatus;
import store.aurora.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {
    @Autowired
    private OrderServiceImpl orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private UserRepository userRepository;

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
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.isExist(null);
        });
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

        Order order = OrderMapper.orderMapper(
                0, LocalDateTime.now(),
                0, 0,
                OrderState.PENDING, "John Doe", "010-1234-5678",
                null, userRepository.getReferenceById("John Doe"));
        Order nonUserOrder = OrderMapper.orderMapper(
                0, LocalDateTime.now(),
                0, 0,
                OrderState.PENDING, "John Doe", "010-1234-5678",
                "", null);

        orderService.createOrder(order);
        orderService.createOrder(nonUserOrder);

        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void createOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(null);
        });
    }

    @Test
    void createOrderWithNullColumns(){
        assertAll(
                ()->{
                    Order o = OrderMapper.orderMapper(
                            null, LocalDateTime.now(),
                            0, 0,
                            OrderState.PENDING, "John Doe", "010-1234-5678",
                            "", null);

                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                ()->{
                    Order o = OrderMapper.orderMapper(
                            0, null,
                            0, 0,
                            OrderState.PENDING, "John Doe", "010-1234-5678",
                            "", null);

                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                ()->{
                    Order o = OrderMapper.orderMapper(
                            0, LocalDateTime.now(),
                            null, 0,
                            OrderState.PENDING, "John Doe", "010-1234-5678",
                            "", null);

                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                ()->{
                    Order o = OrderMapper.orderMapper(
                            0, LocalDateTime.now(),
                            0, null,
                            OrderState.PENDING, "John Doe", "010-1234-5678",
                            "", null);

                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                ()->{
                    Order o = OrderMapper.orderMapper(
                            0, LocalDateTime.now(),
                            0, 0,
                            null, "John Doe", "010-1234-5678",
                            "", null);

                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                ()->{
                    Order o = OrderMapper.orderMapper(
                            0, LocalDateTime.now(),
                            0, 0,
                            OrderState.PENDING, null, "010-1234-5678",
                            "", null);

                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                ()->{
                    Order o = OrderMapper.orderMapper(
                            0, LocalDateTime.now(),
                            0, 0,
                            OrderState.PENDING, "John Doe", null,
                            "", null);

                    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(o));
                },
                ()->{
                    Order o = OrderMapper.orderMapper(
                            0, LocalDateTime.now(),
                            0, 0,
                            OrderState.PENDING, "John Doe", "010-1234-5678",
                            null, null);

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
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrder(null);
        });
    }

    @Test
    void getOrderWithNonExistOrderId() {
        when(orderRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrder(1L);
        });
    }

    @Test
    void getOrders() {
        orderService.getOrders();
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void updateOrder() {
        Order order = OrderMapper.orderMapper(
                0, LocalDateTime.now(),
                0, 0,
                OrderState.PENDING, "John Doe", "010-1234-5678",
                "", null);

        // 테스트를 위해서 임의로 지정한 값
        // 실제 코드에서 작성하면 안됨
        order.setId(1L);

        when(orderRepository.existsById(anyLong())).thenReturn(true);

        orderService.updateOrder(order);

        verify(orderRepository, times(1)).save(any(Order.class));
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

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.deleteOrderById(1L);
        });
    }
}