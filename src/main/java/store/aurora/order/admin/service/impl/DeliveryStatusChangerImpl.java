package store.aurora.order.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.order.admin.service.DeliveryStatusChanger;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.entity.enums.ShipmentState;
import store.aurora.order.service.OrderService;
import store.aurora.order.service.ShipmentService;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class DeliveryStatusChangerImpl implements DeliveryStatusChanger {

    private final OrderService orderService;
    private final ShipmentService shipmentService;
    private final ConcurrentMap<Long, Long> orderScheduleMap = new ConcurrentHashMap<>();

    private static final long ONE_DAY = 86400000;
    private static final long HALF_HOUR = 1800000;

    @Override
    @Transactional
    public void updateOrderStatusToShipping(Long orderId) {
        Order order = orderService.getOrder(orderId);

        order.setState(OrderState.SHIPPING);
        orderService.updateOrder(order);

        Shipment shipment = order.getOrderDetails().getFirst().getShipment();
        shipment.setState(ShipmentState.SHIPPING);
        shipment.setShipmentDatetime(LocalDateTime.now());
        shipmentService.updateShipment(shipment);

        orderScheduleMap.put(orderId, System.currentTimeMillis());
    }

    @Override
    @Transactional
    public void updateOrderStatusToPending(Long orderId) {
        Order order = orderService.getOrder(orderId);

        Shipment shipment = order.getOrderDetails().getFirst().getShipment();
        shipment.setState(ShipmentState.PENDING);
        shipment.setShipmentDatetime(null);
        shipmentService.updateShipment(shipment);

        order.setState(OrderState.PENDING);
        orderService.updateOrder(order);
    }

    @Transactional
    @Scheduled(fixedDelay = HALF_HOUR)
    @Async
    public void scheduleOrderCompletion() {
        long currentTime = System.currentTimeMillis();
        orderScheduleMap.forEach((orderId, startTime) -> {
            if (currentTime - startTime >= ONE_DAY) { // 1일 후에 실행
                completeOrder(orderId);
                orderScheduleMap.remove(orderId);
            }
        });
    }

    @Transactional
    @Override
    public void completeOrder(Long orderId) {
        Order order = orderService.getOrder(orderId);

        if (order.getState().equals(OrderState.SHIPPING)) {
            order.setState(OrderState.SHIPPED);
            orderService.updateOrder(order);
        }
    }
}
