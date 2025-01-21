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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminDeliveryServiceImpl implements AdminDeliveryService {
    private final OrderService orderService;
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

        Long orderId = order.getId();
        String shipmentState = order.getState().toString();


        LocalDateTime shipmentDatetime = order.getOrderDetails().getFirst().getShipment().getShipmentDatetime();
        String shipmentDateString = null;
        if(Objects.nonNull(shipmentDatetime)){
            shipmentDateString = shipmentDatetime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));
        }

        LocalDate preferShipmentDateTime = order.getPreferredDeliveryDate();
        String preferDate = null;
        if(Objects.nonNull(preferShipmentDateTime)){
            preferDate = preferShipmentDateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        }

        List<AdminOrderDetailDTO> orderDetailList = new ArrayList<>();
        for (OrderDetail detail : order.getOrderDetails()) {
            orderDetailList.add(convertToAdminOrderDetailDTO(detail));
        }

        return AdminOrderDTO.builder()
                .orderId(orderId)
                .shipmentState(shipmentState)
                .shipmentDate(shipmentDateString)
                .preferShipmentDate(preferDate)
                .orderDetailList(orderDetailList)
                .build();
    }

    private AdminOrderDetailDTO convertToAdminOrderDetailDTO(OrderDetail detail) {
        return AdminOrderDetailDTO.builder()
                .bookName(detail.getBook().getTitle())
                .price(detail.getAmountDetail())
                .quantity(detail.getQuantity())
                .build();

    }

    @Override
    @Transactional
    public void updateShipmentStatusOfOrder(Long orderId, String shipmentStatus){
        if (shipmentStatus.equals("SHIPPING")) {
            deliveryStatusChanger.updateOrderStatusToShipping(orderId);
        } else if (shipmentStatus.equals("PENDING")) {
            deliveryStatusChanger.updateOrderStatusToPending(orderId);
        }
    }
}