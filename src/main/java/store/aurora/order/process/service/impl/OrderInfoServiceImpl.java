package store.aurora.order.process.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.order.dto.OrderRequestDto;
import store.aurora.order.process.service.OrderInfoService;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderInfoServiceImpl implements OrderInfoService {
    private final RedisTemplate<String, OrderRequestDto> orderRedisTemplate;

    @Override
    @Transactional
    public void saveOrderInfoInRedisWithUuid(String uuid, OrderRequestDto orderInfo) {
        orderRedisTemplate.opsForValue()
                .set(uuid, orderInfo,
                    30, TimeUnit.MINUTES);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderRequestDto getOrderInfoFromRedis(String uuid) {
        return orderRedisTemplate.opsForValue().get(uuid);
    }
}
