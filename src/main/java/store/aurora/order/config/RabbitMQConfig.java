package store.aurora.order.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.MessageListener;

@Configuration
public class RabbitMQConfig {

    static final String SHIPPING_QUEUE = "shippingQueue";

    // todo 실제 배포 환경에선 durable을 true로 설정해야함
    @Bean
    public Queue deliveryQueue(){
        return new Queue(SHIPPING_QUEUE, false);
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListener listenerAdapter){

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(SHIPPING_QUEUE);
        container.setMessageListener(listenerAdapter);

        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(ShippingStatusListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
