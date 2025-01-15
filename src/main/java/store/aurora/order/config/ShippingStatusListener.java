package store.aurora.order.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import store.aurora.order.admin.service.DeliveryStatusChanger;

@Component
@RequiredArgsConstructor
public class ShippingStatusListener {

    private final DeliveryStatusChanger orderService;

    @RabbitListener(queues = RabbitMQConfig.SHIPPING_QUEUE)
    public void receiveMessage(Long orderId){
        orderService.updateOrderStatusToShipping(orderId);
    }
}
