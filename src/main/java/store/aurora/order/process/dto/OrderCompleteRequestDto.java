package store.aurora.order.process.dto;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderCompleteRequestDto {
    private String orderId;
    private String paymentKey;
    private Integer amount;
    @Null
    private Boolean isGuest;
}
