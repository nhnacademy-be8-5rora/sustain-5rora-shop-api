package store.aurora.point.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.aurora.order.entity.Order;
import store.aurora.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Integer pointAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType pointType;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @ManyToOne
    private Order order; // 주문 시 적립이면 있음

    @ManyToOne
    private PointPolicy pointPolicy; // 적립 사유 가져오려고 / 주문 시 적립이면 null

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 주문 외 적립
    public PointHistory(Integer pointAmount, PointType pointType, User user, PointPolicy pointPolicy) {
        this.pointAmount = pointAmount;
        this.pointType = pointType;
        this.user = user;
        this.pointPolicy = pointPolicy;
    }

    // 주문 적립
    public PointHistory(Integer pointAmount, PointType pointType, User user, Order order) {
        this.pointAmount = pointAmount;
        this.pointType = pointType;
        this.user = user;
        this.order = order;
    }
}
