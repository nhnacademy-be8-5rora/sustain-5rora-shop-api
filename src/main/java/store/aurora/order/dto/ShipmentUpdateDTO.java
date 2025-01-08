package store.aurora.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ShipmentUpdateDTO {
    private Long id;
    private String state;
    private String trackingNumber;
    private String shippingCompany;
    private LocalDateTime shipmentDateTime;
}
