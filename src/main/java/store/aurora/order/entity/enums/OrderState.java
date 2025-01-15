package store.aurora.order.entity.enums;

import store.aurora.order.exception.exception404.OrderStateConvertFailException;

// todo: 부분 취소 상태 추가
public enum OrderState {
    PENDING,    // 대기
    CANCELLED,  // 취소됨
    SHIPPING,    // 배송중
    SHIPPED,  // 배송 완료
    REFUND_PENDING, // 환불 대기
    REFUNDED,   // 환불됨
    CONFIRMED;  // 주문 확정

    public static OrderState fromOrdinal(int ordinal) {
        for (OrderState state : values()) {
            if (state.ordinal() == ordinal) {
                return state;
            }
        }
        throw new OrderStateConvertFailException("Invalid ordinal for OrderState: " + ordinal);
    }
}
