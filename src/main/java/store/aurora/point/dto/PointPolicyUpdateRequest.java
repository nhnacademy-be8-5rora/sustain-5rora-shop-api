package store.aurora.point.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import store.aurora.point.entity.PointPolicyType;

import java.math.BigDecimal;

@Getter
@Setter
public class PointPolicyUpdateRequest {
//    private String pointPolicyName;
//    private PointPolicyType pointPolicyType;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal pointPolicyValue;
}