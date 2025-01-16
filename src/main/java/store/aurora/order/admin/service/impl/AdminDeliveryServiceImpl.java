package store.aurora.order.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.order.admin.dto.AdminOrderDTO;
import store.aurora.order.admin.dto.AdminOrderDetailDTO;
import store.aurora.order.admin.service.AdminDeliveryService;
import store.aurora.order.admin.service.DeliveryStatusChanger;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.service.OrderService;
import store.aurora.order.service.ShipmentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminDeliveryServiceImpl implements AdminDeliveryService {
    private final OrderService orderService;
    private final ShipmentService shipmentService;
    private final DeliveryStatusChanger deliveryStatusChanger;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminOrderDTO> getAllOrderList(Pageable pageable) {
        List<Order> orders = orderService.getOrders();
        List<AdminOrderDTO> orderDTOList = orders.stream()
                .filter(order -> !order.getState().equals(OrderState.CANCELLED))
                .map(this::convertToAdminOrderDTO)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orderDTOList.size());
        return new PageImpl<>(orderDTOList.subList(start, end), pageable, orderDTOList.size());
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

    @Override
    @Transactional
    public void updateShipmentStatusOfOrder(Long orderId, String shipmentStatus){
        if (shipmentStatus.equals("SHIPPING")) {
            deliveryStatusChanger.updateOrderStatusToShipping(orderId);
        } else if (shipmentStatus.equals("PENDING")) {
            deliveryStatusChanger.updateOrderStatusToPending(orderId);
        }

//        OrderState orderState = OrderState.valueOf(shipmentStatus);
//        order.setState(OrderState.valueOf(orderState.toString()));
//
//        List<OrderDetail> orderDetails = order.getOrderDetails();
//        for (OrderDetail orderDetail : orderDetails) {
//            orderDetail.setState(OrderState.valueOf(shipmentStatus));
//        }
//
//        Shipment shipment = orderDetails.getFirst().getShipment();
//        shipment.setState(ShipmentState.valueOf(shipmentStatus));
//        shipment.setShipmentDatetime(LocalDateTime.now());
//        shipmentService.updateShipment(shipment);
//
//        deliveryStatusChanger.updateOrderStatusToShipping(orderId);
    }
}