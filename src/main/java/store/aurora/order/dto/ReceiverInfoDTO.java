package store.aurora.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ReceiverInfoDTO {
    private String name;
    private String phone;
    private String address;
    private String detailAddress;
    private String deliveryMessage;
}
