package store.aurora.order.dto;

import store.aurora.order.entity.enums.OrderState;

import java.time.LocalDateTime;

public record OrderInfoDto(
        Long orderId,
        Integer totalAmount,
        OrderState orderState,
        LocalDateTime orderTime,
        String orderContent) {
}
