package store.aurora.order.config;

import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import store.aurora.order.dto.OrderRequestDto;

@Configuration
public class OrderRedisConfig {

    @Bean(name = "orderRedisTemplate")
    public RedisTemplate<String, OrderRequestDto> orderRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, OrderRequestDto> template = new RedisTemplate<>();

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<OrderRequestDto> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(OrderRequestDto.class);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
