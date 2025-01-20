package store.aurora.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_ranks")
public class UserRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_name", nullable = false)
    private Rank rankName;

    @Column(name = "rank_min_amount", nullable = false)
    private Integer minAmount;

    @Column(name = "rank_max_amount")
    private Integer maxAmount;

    @Column(name = "point_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal pointRate;
}
