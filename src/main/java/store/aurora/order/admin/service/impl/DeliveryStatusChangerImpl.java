package store.aurora.order.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.order.admin.service.DeliveryStatusChanger;
import store.aurora.order.config.RabbitMQConfig;
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

    private static final String PENDING = "PENDING";
    private static final String SHIPPING = "SHIPPING";
    private static final String SHIPPED = "SHIPPED";

    @Override
    @Transactional
    public void updateOrderStatusToShipping(Long orderId) {
        changeShipmentState(orderService.getOrder(orderId), SHIPPING);

        rabbitTemplate.convertAndSend("", RabbitMQConfig.SHIPPING_QUEUE, orderId);
    }

    @Override
    @Transactional
    public void updateOrderStatusToPending(Long orderId) {
        changeShipmentState(orderService.getOrder(orderId), PENDING);
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderService.getOrder(orderId);

        if (order.getState().equals(OrderState.SHIPPING)) {
            changeShipmentState(order, SHIPPED);
        }
    }

    private void changeShipmentState(Order order, String state){
        if(!(state.equals(PENDING) || state.equals(SHIPPING) || state.equals(SHIPPED))){
            return;
        }
        order.setState(OrderState.valueOf(state));
        orderService.updateOrder(order);

        Shipment shipment = order.getOrderDetails().getFirst().getShipment();
        shipment.setState(ShipmentState.valueOf(state));

        if(state.equals(PENDING)){
            shipment.setShipmentDatetime(null);
        } else if(state.equals(SHIPPING)){
            shipment.setShipmentDatetime(LocalDateTime.now());
        }

        shipmentService.updateShipment(shipment);
    }
}
