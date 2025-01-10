package store.aurora.point.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.aurora.point.entity.PointPolicyType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointPolicyRequest {
    @NotNull @Min(1)
    private Integer pointPolicyId;

    @NotBlank(message = "pointPolicyName은 공백일 수 없습니다.")
    @Size(max = 50)
    private String pointPolicyName;

    @NotNull
    private PointPolicyType pointPolicyType;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal pointPolicyValue;
}