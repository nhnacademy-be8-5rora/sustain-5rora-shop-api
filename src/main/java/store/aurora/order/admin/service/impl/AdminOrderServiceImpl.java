package store.aurora.order.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.order.admin.dto.AdminOrderDTO;
import store.aurora.order.admin.dto.AdminOrderDetailDTO;
import store.aurora.order.admin.service.AdminOrderService;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.service.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public List<AdminOrderDTO> getAllOrderList() {
        List<Order> orderList = orderService.getOrders();
        List<AdminOrderDTO> orderDtoList = new ArrayList<>();

        for (Order order : orderList) {
            AdminOrderDTO orderDto = convertToAdminOrderDTO(order);
            orderDtoList.add(orderDto);
        }

        return orderDtoList;
    }

    private AdminOrderDTO convertToAdminOrderDTO(Order order) {
        AdminOrderDTO orderDto = new AdminOrderDTO();
        orderDto.setOrderId(order.getId());
        orderDto.setShipmentState(order.getState().toString());

        List<AdminOrderDetailDTO> detailList = new ArrayList<>();
        for (OrderDetail detail : order.getOrderDetails()) {
            AdminOrderDetailDTO detailDto = convertToAdminOrderDetailDTO(detail);
            detailList.add(detailDto);
        }

        orderDto.setOrderDetailList(detailList);
        return orderDto;
    }

    private AdminOrderDetailDTO convertToAdminOrderDetailDTO(OrderDetail detail) {
        String shipmentDatetime = null;
        if (Objects.nonNull(detail.getShipment()) && Objects.nonNull(detail.getShipment().getShipmentDatetime())) {
            shipmentDatetime = detail.getShipment().getShipmentDatetime().toString();
        }

        return new AdminOrderDetailDTO(
                detail.getId(),
                detail.getState().toString(),
                shipmentDatetime
        );
    }
}