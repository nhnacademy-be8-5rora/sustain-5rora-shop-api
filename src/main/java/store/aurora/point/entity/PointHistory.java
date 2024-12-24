package store.aurora.point.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.aurora.order.entity.Order;

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
    @Setter
    @Column(nullable = false)
    private Integer pointAmount;

    @NotNull
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType pointType;

    @Setter
    private String description;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Setter
    @ManyToOne
    private Order order;

    @Setter
    @Enumerated(EnumType.STRING)
    private PointEarnStatus pointEarnStatus;

    public PointHistory(Integer pointAmount, PointType pointType) {
        this.pointAmount = pointAmount;
        this.pointType = pointType;
    }
}
