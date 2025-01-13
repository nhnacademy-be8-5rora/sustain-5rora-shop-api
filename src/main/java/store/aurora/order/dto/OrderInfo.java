package store.aurora.order.dto;

import java.time.LocalDateTime;

public interface OrderInfo {
    Long getOrderId();
    Integer getTotalAmount();
    Integer getOrderState();
    LocalDateTime getOrderTime();
    String getOrderContent();
}
