package store.aurora.order.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class AdminOrderDTO{
    private Long orderId;
    private String shipmentState;
    private List<AdminOrderDetailDTO> orderDetailList;
}
