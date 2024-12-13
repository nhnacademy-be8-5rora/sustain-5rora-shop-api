package store.aurora.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    @NotNull
    @Column(name = "delivery_fee")
    private int deliveryFee;

    // 주문 시각
    @NotNull
    @Column(name = "order_time")
    private LocalDateTime orderTime;

    // 총 주문 금액, default 0
    @NotNull
    @Column(name = "total_amount")
    private int totalAmount;

    // 포인트 사용량, default 0
    @NotNull
    @Column(name = "point_amount")
    private int pointAmount;

    // 주문 상태
    @NotNull
    @Column(name = "state")
    private OrderState state;

    // 주문자 이름
    @NotNull
    @Column(name = "name")
    private String name;

    // 주문자 전화 번호
    @NotNull
    @Column(name = "order_phone")
    private String orderPhone;

    // 주문자 이메일
    @Email
    @NotNull
    @Column(name = "order_email")
    private String orderEmail;

    // 비회원 주문자 비밀번호, 회원 주문 시에는 null
    @Column(name = "password")
    private String password;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order")
    private ShipmentInformation shipmentInformation;

    @OneToMany(mappedBy = "order")
    private List<Payment> payments;
}
