package store.aurora.order.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

@Service
@RequiredArgsConstructor
public class DeliveryStatusChangerImpl implements DeliveryStatusChanger {

    private final OrderService orderService;
    private final ShipmentService shipmentService;
    private final RabbitTemplate rabbitTemplate;

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

        rabbitTemplate.convertAndSend("shippingQueue", orderId);
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
    @Override
    public void completeOrder(Long orderId) {
        Order order = orderService.getOrder(orderId);

        if (order.getState().equals(OrderState.SHIPPING)) {
            order.setState(OrderState.SHIPPED);
            orderService.updateOrder(order);
        }
    }
}
