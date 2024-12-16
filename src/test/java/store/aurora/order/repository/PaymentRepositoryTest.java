package store.aurora.order.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.Payment;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.PaymentState;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;

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