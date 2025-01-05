package store.aurora.point.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "point_policies")
@NoArgsConstructor
@Getter
public class PointPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String pointPolicyName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointPolicyType pointPolicyType;

    @Setter
    @NotNull
    @Column(nullable = false, precision = 10, scale = 1)
    private BigDecimal pointPolicyValue;

    public PointPolicy(String pointPolicyName, PointPolicyType pointPolicyType, BigDecimal pointPolicyValue) {
        this.pointPolicyName = pointPolicyName;
        this.pointPolicyType = pointPolicyType;
        this.pointPolicyValue = pointPolicyValue;
    }
}
