package store.aurora.order.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Payment;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.PaymentState;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Import(QuerydslConfiguration.class)
@DataJpaTest
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private OrderDetailRepository orderDetailRepository;

    @Test
    void findByOrder() {
        // Arrange
        Order order = new Order();
        order.setDeliveryFee(1000);
        order.setDeliveryFee(0);
        order.setOrderTime(LocalDateTime.now());
        order.setTotalAmount(50000);
        order.setPointAmount(0);
        order.setState(OrderState.CONFIRMED);
        order.setName("John Doe");
        order.setOrderPhone("010-1234-5678");
        order.setOrderEmail("johndoe@example.com");
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(PaymentState.PENDING);
        payment.setOrder(order);
        payment.setAmount(100);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(1L);
        orderDetail.setOrder(order);
        orderDetail.setState(OrderState.CONFIRMED);
        orderDetail.setAmountDetail(1000);
        orderDetail.setQuantity(1);
        orderDetail.setBook(null);
        orderDetail.setShipment(null);
        when(orderDetailRepository.findByOrder(any(Order.class))).thenReturn(List.of(orderDetail));
        when(orderDetailRepository.getReferenceById(anyLong())).thenReturn(orderDetail);
        paymentRepository.save(payment);

        // Act
        List<Payment> retrievedPayments = paymentRepository.findByOrder(order);
        Payment retrievedPayment = retrievedPayments.getFirst();

        // Assert
        assertNotNull(retrievedPayment);
        assertEquals(payment.getId(), retrievedPayment.getId());
        assertEquals(payment.getAmount(), retrievedPayment.getAmount());
    }
}