package store.aurora.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import store.aurora.book.entity.Book;
import store.aurora.order.entity.enums.OrderState;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // 주문 상태
    @Column(name = "state", nullable = false)
    private OrderState state;

    // 결제 금액 (쿠폰 적용가)
    @Column(name = "amount_detail", nullable = false)
    private Integer amountDetail;

    // 수량
    @Min(0)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // 포장지
    @ManyToOne
    @JoinColumn(name = "wrap_id")
    private Wrap wrap;

    // 쿠폰
    @Column(name = "coupon_id")
    private Long couponId;

    // 책
    @ManyToOne
    @JoinColumn(name = "book_id")
//    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 배송
    @ManyToOne
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    // 환불 또는 취소 일자
    @Column(name = "refund_or_cancel_date")
    private LocalDate refundOrCancelDate;
}
