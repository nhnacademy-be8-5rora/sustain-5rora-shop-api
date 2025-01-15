package store.aurora.order.dto;

import lombok.*;
import store.aurora.order.entity.enums.OrderState;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
public class OrderRelatedInfoWithAuth {
    private Long orderId;
    private LocalDate prefferedDeliveryDate;
    private Integer deliveryFee;
    private LocalDateTime orderTime;
    private Integer totalAmount;
    private Integer pointAmount;
    private OrderState orderState;
    private String password;
    private String userId;

    private String orderPhone;
    private String orderEmail;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String customerRequest;
}
