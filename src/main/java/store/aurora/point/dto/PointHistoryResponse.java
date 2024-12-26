package store.aurora.point.dto;

import lombok.*;
import store.aurora.point.entity.PointType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointHistoryResponse {
    private Long id;
    private Integer pointAmount;
    private PointType pointType;
    private LocalDateTime transactionDate;
    private String from; // 결제 시 주문 ID, 그 외 정책 이름
}
