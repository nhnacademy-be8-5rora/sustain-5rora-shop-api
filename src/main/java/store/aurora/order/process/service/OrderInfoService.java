package store.aurora.order.process.service;

import store.aurora.order.dto.OrderRequestDto;

public interface OrderInfoService {
    /**
     * 주문 정보를 uuid를 key로 하여 redis에 저장
     * @param uuid 주문 번호
     * @param orderInfo 주문 정보
     */
    void saveOrderInfoInRedisWithUuid(String uuid, OrderRequestDto orderInfo);

    OrderRequestDto getOrderInfoFromRedis(String uuid);
}
