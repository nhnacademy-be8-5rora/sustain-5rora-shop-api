package store.aurora.order.entity.enums;

import store.aurora.order.exception.exception404.OrderStateConvertFailException;

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

    public static OrderState fromString(String name){
        for (OrderState state : values()){
            if (state.name().equals(name)){
                return state;
            }
        }
        throw new OrderStateConvertFailException("Invalid name for OrderState: " + name);
    }
}
