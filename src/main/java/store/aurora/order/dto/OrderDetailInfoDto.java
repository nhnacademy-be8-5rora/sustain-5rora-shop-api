package store.aurora.order.dto;

import lombok.*;
import store.aurora.order.entity.enums.OrderState;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class OrderDetailInfoDto {
    private String bookTitle;
    private Integer bookPrice; //구매 당시의 판매가
    private Integer quantity;
    private Long couponId;
    private String wrapName;
    private Integer wrapPrice;
    private OrderState orderDetailState;
    private Long shipmentId;
}
