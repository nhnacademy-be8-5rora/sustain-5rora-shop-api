package store.aurora.point.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 정책 타입", enumAsRef = true, example = "AMOUNT")
public enum PointPolicyType {
    PERCENTAGE,
    AMOUNT
}