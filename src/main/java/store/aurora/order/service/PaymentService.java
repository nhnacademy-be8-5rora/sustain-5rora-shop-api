package store.aurora.order.service;

import store.aurora.order.entity.Payment;

public interface PaymentService {
    Payment createPayment(Payment payment);
}
