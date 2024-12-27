package store.aurora.order.exception.exception404;

import store.aurora.common.exception.DataNotFoundException;

public class OrderDetailNotFoundException extends DataNotFoundException {
    public OrderDetailNotFoundException(String orderDetailId) {
        super("OrderDetail Not Found Exception, Id: " + orderDetailId);
    }

    public OrderDetailNotFoundException(Long orderDetailId) {
        super("OrderDetail Not Found Exception, Id: " + orderDetailId);
    }
}
