package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.order.entity.Payment;
import store.aurora.order.repository.OrderRepository;
import store.aurora.order.repository.PaymentRepository;
import store.aurora.order.service.PaymentService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public Payment createPayment(Payment payment) {
        if(Objects.isNull(payment)){
            throw new IllegalArgumentException("Payment cannot be null");
        }

        if(Objects.isNull(payment.getStatus())){
            throw new IllegalArgumentException("Payment status cannot be null");
        }

        if(Objects.isNull(payment.getOrder())){
            throw new IllegalArgumentException("Payment order cannot be null");
        }

        if(Objects.isNull(payment.getOrder().getId()) || !orderRepository.existsById(payment.getOrder().getId())){
            throw new IllegalArgumentException("Payment order id is invalid");
        }

        return paymentRepository.save(payment);
    }
}
