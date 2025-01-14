package store.aurora.order.exception.exception404;

import store.aurora.common.exception.ConvertFailException;

public class OrderStateConvertFailException extends ConvertFailException {
    public OrderStateConvertFailException(String message) {
        super(message);
    }
}
