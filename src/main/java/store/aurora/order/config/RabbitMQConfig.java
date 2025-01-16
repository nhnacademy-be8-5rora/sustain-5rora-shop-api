package store.aurora.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SHIPPING_QUEUE = "shippingQueue";
    public static final String DLX_QUEUE = "dlxQueue";
    public static final String DLX_EXCHANGE = "dlxExchange";

    private static final Long ONE_DAY = 86400000L;

    @Bean
    public Queue shippingQueue(){
        return QueueBuilder.durable(SHIPPING_QUEUE)
                .withArgument("x-message-ttl", 6000)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .build();
    }

    @Bean
    public Queue dlxQueue(){
        return new Queue(DLX_QUEUE, true);
    }

    @Bean
    public DirectExchange dlxExchange(){
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Binding dlxBinding(){
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with(SHIPPING_QUEUE);
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListener listenerAdapter){

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(DLX_QUEUE);
        container.setMessageListener(listenerAdapter);

        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(ShippingStatusListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
