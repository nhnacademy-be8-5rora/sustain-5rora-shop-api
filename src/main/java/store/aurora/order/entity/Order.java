package store.aurora.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 배송 희망 날짜, 사용자가 선택하지 않으면 null
    @Column(name = "preferred_delivery_date")
    private LocalDate preferredDeliveryDate;

    // 배송비, default 0
    @Column(name = "delivery_fee", nullable = false)
    private Integer deliveryFee;

    // 주문 시각
    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    // 총 주문 금액, default 0
    @ColumnDefault("0")
    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    // 포인트 사용량, default 0
    @ColumnDefault("0")
    @Column(name = "point_amount", nullable = false)
    private Integer pointAmount;

    // 주문 상태
    @Column(name = "state", nullable = false)
    private OrderState state;

    // 주문자 이름
    @Column(name = "name", nullable = false)
    private String name;

    // 주문자 전화 번호
    @Column(name = "order_phone", nullable = false)
    private String orderPhone;

    // 주문자 이메일
    @Email
    @Column(name = "order_email")
    private String orderEmail;

    // 비회원 주문자 비밀번호, 회원 주문 시에는 null
    @Column(name = "password")
    private String password;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addOrderDetail(OrderDetail orderDetail) {
        if (orderDetails == null) {
            orderDetails = new ArrayList<>();
        }
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this); // 양방향 관계 동기화
    }

    @OneToOne(mappedBy = "order")
    private ShipmentInformation shipmentInformation;

    @OneToMany(mappedBy = "order")
    private List<Payment> payments;
}
