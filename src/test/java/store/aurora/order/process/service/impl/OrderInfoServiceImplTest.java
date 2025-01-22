package store.aurora.order.process.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import store.aurora.order.dto.OrderDetailDTO;
import store.aurora.order.dto.OrderRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderInfoServiceImplTest {

    @Mock
    private RedisTemplate<String, OrderRequestDto> orderRedisTemplate;

    @Mock
    private ValueOperations<String, OrderRequestDto> valueOperations;

    @InjectMocks
    private OrderInfoServiceImpl orderInfoService;

    private final String uuid = "test-uuid";
    private OrderRequestDto mockOrder;

    @BeforeEach
    void setUp() {
        when(orderRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // 테스트용 OrderRequestDto 생성
        mockOrder = new OrderRequestDto(
                "user123", "password123",                  // 인증정보
                "홍길동", "010-1234-5678", "hong@example.com", LocalDate.of(2025, 1, 25), // 주문자 정보
                "이순신", "010-9876-5432", "lee@example.com", "서울특별시 강남구", "빠른 배송 부탁드립니다.", // 받는 사람 정보
                List.of(new OrderDetailDTO(1L, 2, 101L, 201L, 3000)), // 상품 정보
                500  // 사용한 포인트
        );
    }

    @Test
    void saveOrderInfoInRedisWithUuid_성공적으로_저장() {
        // when
        orderInfoService.saveOrderInfoInRedisWithUuid(uuid, mockOrder);

        // then
        verify(valueOperations, times(1)).set(uuid, mockOrder, 30, TimeUnit.MINUTES);
    }

    @Test
    void getOrderInfoFromRedis_성공적으로_조회() {
        // given
        when(valueOperations.get(uuid)).thenReturn(mockOrder);

        // when
        OrderRequestDto result = orderInfoService.getOrderInfoFromRedis(uuid);

        // then
        assertThat(result).isEqualTo(mockOrder);
    }

    @Test
    void getOrderInfoFromRedis_없는_데이터_조회시_null_반환() {
        // given
        when(valueOperations.get(uuid)).thenReturn(null);

        // when
        OrderRequestDto result = orderInfoService.getOrderInfoFromRedis(uuid);

        // then
        assertThat(result).isNull();
    }
}
