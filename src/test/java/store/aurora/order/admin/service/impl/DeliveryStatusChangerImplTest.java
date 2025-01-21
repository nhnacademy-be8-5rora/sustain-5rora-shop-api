package store.aurora.order.admin.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import store.aurora.order.config.RabbitMQConfig;
import store.aurora.order.entity.Order;
import store.aurora.order.entity.OrderDetail;
import store.aurora.order.entity.Shipment;
import store.aurora.order.entity.enums.OrderState;
import store.aurora.order.service.OrderService;
import store.aurora.order.service.ShipmentService;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryStatusChangerImplTest {

    @Mock private OrderService orderService;
    @Mock private ShipmentService shipmentService;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks private DeliveryStatusChangerImpl deliveryStatusChanger;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        OrderDetail mockOrderDetail = new OrderDetail();
        mockOrderDetail.setShipment(new Shipment());

        mockOrder.setOrderDetails(List.of(mockOrderDetail));
    }

    @Test
    void updateOrderStatusToShipping() {
        // given
        mockOrder.setId(1L);
        when(orderService.getOrder(1L)).thenReturn(mockOrder);
        doNothing().when(orderService).updateOrder(any(Order.class));

        // when
        deliveryStatusChanger.updateOrderStatusToShipping(1L);

        // then
        verify(orderService).getOrder(1L);
        verify(rabbitTemplate).convertAndSend("", RabbitMQConfig.SHIPPING_QUEUE, 1L);
    }

    @Test
    void updateOrderStatusToPending() {
        // given
        mockOrder.setId(1L);
        when(orderService.getOrder(1L)).thenReturn(mockOrder);
        doNothing().when(orderService).updateOrder(any(Order.class));

        // when
        deliveryStatusChanger.updateOrderStatusToPending(1L);

        // then
        verify(orderService).getOrder(1L);
    }

    @Test
    void completeOrder() {
        // given
        mockOrder.setId(1L);
        mockOrder.setState(OrderState.SHIPPING);
        when(orderService.getOrder(1L)).thenReturn(mockOrder);
        doNothing().when(orderService).updateOrder(any(Order.class));

        // when
        deliveryStatusChanger.completeOrder(1L);

        // then
        verify(orderService).getOrder(1L);
        verify(orderService).updateOrder(any(Order.class));
    }
}