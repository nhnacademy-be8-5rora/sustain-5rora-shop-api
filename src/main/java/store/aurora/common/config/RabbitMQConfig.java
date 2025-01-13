package store.aurora.common.config;

import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue paymentQueue(){
        return new Queue("payment-queue", true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory,
                                                            MessageListenerAdapter listenerAdapter){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("payment-queue");
        container.setMessageListener((MessageListener) listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(PaymentMessageListener listener) {
        return new MessageListenerAdapter(listener, "handleMessage");
    }
}
