package store.aurora.order.admin.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class AdminOrderDTO{
    private Long orderId;
    private String shipmentState;
    private String shipmentDate;
    private String preferShipmentDate;
    private List<AdminOrderDetailDTO> orderDetailList;
}
