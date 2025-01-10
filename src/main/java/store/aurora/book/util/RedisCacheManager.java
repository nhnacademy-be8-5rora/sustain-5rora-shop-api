package store.aurora.book.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisCacheManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger USER_LOG = LoggerFactory.getLogger("redis-logger");

    public void store(String key, Object data, Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, data, duration);
            USER_LOG.info("Redis에 데이터를 저장했습니다. 키: {}", key);
        } catch (RedisConnectionException e) {
            USER_LOG.warn("Redis 연결 실패. 데이터를 저장하지 못했습니다. 키: {}", key, e);
        }
    }

    public <T> T get(String key, TypeReference<T> typeReference) {
        try {
            Object cachedData = redisTemplate.opsForValue().get(key);
            if (cachedData != null) {
                return objectMapper.convertValue(cachedData, typeReference);
            }
        } catch (RedisConnectionException e) {
            USER_LOG.warn("Redis 연결 실패. 데이터를 가져오지 못했습니다. 키: {}", key, e);
        } catch (IllegalArgumentException e) {
            USER_LOG.warn("Redis 데이터 타입 변환 실패. 키: {}", key, e);
        }
        return null;
    }
}