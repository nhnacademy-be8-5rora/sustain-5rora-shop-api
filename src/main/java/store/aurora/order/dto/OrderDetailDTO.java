package store.aurora.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderDetailDTO {
    private Long bookId;
    private Integer quantity;
    private Long wrapId;
    private Long couponId;
    // 할인 받는 금액, front에서 계산 한 값을 받음 (front에서 할인 금액 계산 할 필요는 없음)
    private Integer discountAmount;
}
