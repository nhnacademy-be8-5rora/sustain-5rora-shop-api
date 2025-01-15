package store.aurora.order.entity.enums;

public enum ShipmentState {
    PENDING,    // 대기
    SHIPPING,   // 배송중
    SHIPPED,    // 배송 완료
    CANCELLED,     // 배송 전 취소
}
