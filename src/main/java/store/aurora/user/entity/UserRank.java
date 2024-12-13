package store.aurora.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_ranks")
public class UserRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "rank_name", nullable = false)
    private String rankName;

    @Column(name = "rank_min_amount", nullable = false)
    private int minAmount;

    @Column(name = "rank_max_amount", nullable = false)
    private int maxAmount;

    @Column(name = "point_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal pointRate;
}
