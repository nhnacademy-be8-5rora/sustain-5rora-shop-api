package store.aurora.order.entity.enums;

public enum OrderState {
    PENDING,    // 대기
    CANCELLED,  // 취소됨
    SHIPPED,    // 배송중
    DELIVERED,  // 배송 완료
    REFUND_PENDING, // 환불 대기
    REFUNDED,   // 환불됨
    CONFIRMED;  // 주문 확정

    public static OrderState fromString(String state) {
        return switch (state.toUpperCase()) {
            case "PENDING" -> PENDING;
            case "CANCELLED" -> CANCELLED;
            case "SHIPPED" -> SHIPPED;
            case "DELIVERED" -> DELIVERED;
            case "REFUND_PENDING" -> REFUND_PENDING;
            case "REFUNDED" -> REFUNDED;
            case "CONFIRMED" -> CONFIRMED;
            default -> throw new IllegalArgumentException("Unknown order state: " + state);
        };
    }
}
