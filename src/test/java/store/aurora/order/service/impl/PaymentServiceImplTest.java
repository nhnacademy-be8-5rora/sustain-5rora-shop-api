package store.aurora.order.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.Payment;
import store.aurora.order.entity.enums.PaymentState;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.repository.PaymentRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    PaymentRepository paymentRepository;
    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    PaymentServiceImpl paymentService;

    @Test
    void createPayment() {
        when(orderRepository.existsById(anyLong())).thenReturn(true);

        Order mockOrder = new Order();
        mockOrder.setId(1L);

        Payment payment = Payment.builder()
                .amount(10000)
                .paymentDatetime(LocalDateTime.now())
                .status(PaymentState.COMPLETED)
                .order(mockOrder)
                .build();

        paymentService.createPayment(payment);

        verify(paymentRepository).save(payment);
    }

    @Test
    void createPaymentWithNullPayment() {
        assertThatThrownBy(() -> paymentService.createPayment(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment cannot be null");
    }

    @Test
    void createPaymentWithNullPaymentStatus() {
        Payment payment = Payment.builder()
                .amount(10000)
                .paymentDatetime(LocalDateTime.now())
                .order(new Order())
                .build();

        assertThatThrownBy(() -> paymentService.createPayment(payment))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment status cannot be null");
    }

    @Test
    void createPaymentWithNullPaymentOrder() {
        Payment payment = Payment.builder()
                .amount(10000)
                .paymentDatetime(LocalDateTime.now())
                .status(PaymentState.COMPLETED)
                .build();

        assertThatThrownBy(() -> paymentService.createPayment(payment))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment order cannot be null");
    }

    @Test
    void createPaymentWithInvalidPaymentOrder() {
        Order mockOrder = new Order();
        mockOrder.setId(1L);

        Payment payment = Payment.builder()
                .amount(10000)
                .paymentDatetime(LocalDateTime.now())
                .status(PaymentState.COMPLETED)
                .order(mockOrder)
                .build();

        when(orderRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> paymentService.createPayment(payment))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment order id is invalid");
    }

    @Test
    void createPaymentWithNullPaymentOrderId() {
        Order mockOrder = new Order();

        Payment payment = Payment.builder()
                .amount(10000)
                .paymentDatetime(LocalDateTime.now())
                .status(PaymentState.COMPLETED)
                .order(mockOrder)
                .build();

        assertThatThrownBy(() -> paymentService.createPayment(payment))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment order id is invalid");
    }
}