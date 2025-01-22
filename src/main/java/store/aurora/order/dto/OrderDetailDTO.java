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
    // 할인 받는 금액
    private Integer discountAmount;
}
