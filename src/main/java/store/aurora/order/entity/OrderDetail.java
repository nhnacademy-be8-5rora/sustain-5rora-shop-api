package store.aurora.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.book.entity.Book;
import store.aurora.order.entity.enums.OrderState;

import java.time.LocalDate;

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
    @NotNull
    @Column(name = "state")
    private OrderState state;

    // 결제 금액 (쿠폰 적용가)
    @NotNull
    @Column(name = "amount_detail")
    private int amountDetail;

    // 수량
    @NotNull
    @Column(name = "quantity")
    private int quantity;

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
    private Book book;

    // 배송
    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    // 환불 또는 취소 일자
    @Column(name = "refund_or_cancel_date")
    private LocalDate refundOrCancelDate;
}
