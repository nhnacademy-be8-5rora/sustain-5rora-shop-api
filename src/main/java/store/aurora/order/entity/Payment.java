package store.aurora.order.entity;

import jakarta.persistence.*;
import lombok.*;
import store.aurora.order.entity.enums.PaymentState;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 결제 금액
    @Column(name = "amount", nullable = false)
    private int amount;

    // 결제 시간
    @Column(name = "payment_datetime")
    private LocalDateTime paymentDatetime;

    // 결제 상태
    @Column(name = "status", nullable = false)
    private PaymentState status;

    // 주문
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // 결제타입 + 키 (ex. NAVER_12312412412312)
    @Column(name = "payment_key")
    private String paymentKey;
}
