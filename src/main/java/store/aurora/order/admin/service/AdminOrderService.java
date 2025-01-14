package store.aurora.order.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.aurora.order.admin.dto.AdminOrderDTO;

public interface AdminOrderService {
    Page<AdminOrderDTO> getAllOrderList(Pageable pageable);

    void updateShipmentStatusOfOrder(Long orderId, String shipmentState);
}
