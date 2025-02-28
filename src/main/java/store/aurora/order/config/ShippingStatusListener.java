package store.aurora.order.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import store.aurora.order.admin.service.DeliveryStatusChanger;

@Component
@RequiredArgsConstructor
public class ShippingStatusListener {

    private final DeliveryStatusChanger deliveryStatusChanger;

    @RabbitListener(queues = RabbitMQConfig.DLX_QUEUE)
    public void receiveMessage(Long orderId){
        deliveryStatusChanger.completeOrder(orderId);
    }
}
