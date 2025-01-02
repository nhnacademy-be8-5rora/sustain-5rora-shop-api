package store.aurora.order.exception.exception404;

import store.aurora.common.exception.DataNotFoundException;

public class OrderNotFoundException extends DataNotFoundException {
    public OrderNotFoundException(Long orderId) {
        super("Order Not Found Exception, Id: " + orderId);
    }
    public OrderNotFoundException(String orderId) {
        super("Order Not Found Exception, Id: " + orderId);
    }
}
