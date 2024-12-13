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

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull
    @Column(name = "state")
    private OrderState state;

    @NotNull
    @Column(name = "amount_detail")
    private int amountDetail;

    @NotNull
    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "wrap_id")
    private Wrap wrap;

    @Column(name = "coupon_id")
    private Long couponId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @Column(name = "refund_or_cancel_date")
    private LocalDate refundOrCancelDate;
}
