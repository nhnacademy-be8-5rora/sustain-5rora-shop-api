package store.aurora.order.config;

import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@Configuration
public class OrderRedisConfig {

    @Bean(name = "orderRedisTemplate")
    public RedisTemplate<String, Map<String, Object>> orderRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Map<String, Object>> template = new RedisTemplate<>();

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Map<String, Object>> jackson2JsonRedisSerializer
                = new Jackson2JsonRedisSerializer<>
                (TypeFactory.defaultInstance()
                            .constructMapType(Map.class, String.class, Object.class));

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
