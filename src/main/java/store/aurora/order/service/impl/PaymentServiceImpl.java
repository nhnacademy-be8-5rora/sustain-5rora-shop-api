package store.aurora.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.order.entity.Payment;
import store.aurora.order.repository.PaymentRepository;
import store.aurora.order.service.PaymentService;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.getReferenceById(paymentId);
    }

    @Override
    public void deletePaymentById(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }
}
