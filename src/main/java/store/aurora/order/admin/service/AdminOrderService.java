package store.aurora.order.admin.service;

import store.aurora.order.admin.dto.AdminOrderDTO;

import java.util.List;

public interface AdminOrderService {
    List<AdminOrderDTO> getAllOrderList();
}
