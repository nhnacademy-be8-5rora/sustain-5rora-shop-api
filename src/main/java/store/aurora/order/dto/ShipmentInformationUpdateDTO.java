package store.aurora.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ShipmentInformationUpdateDTO {
    private Long orderId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String customerRequest;
}
