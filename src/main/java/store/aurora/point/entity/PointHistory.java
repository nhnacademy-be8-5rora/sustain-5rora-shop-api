package store.aurora.point.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_histories")
@NoArgsConstructor
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

//    @Setter
//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    private Order order;
    private Long orderId;

    @Setter
    @Enumerated(EnumType.STRING)
    private PointEarnStatus pointEarnStatus;

    public PointHistory(Integer pointAmount, PointType pointType) {
        this.pointAmount = pointAmount;
        this.pointType = pointType;
    }
}
