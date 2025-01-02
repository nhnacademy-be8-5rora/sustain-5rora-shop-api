package store.aurora.order.exception.exception404;

import store.aurora.common.exception.DataNotFoundException;

public class WrapNotFoundException extends DataNotFoundException {
    public WrapNotFoundException(Long wrapId) {
        super("Order Not Found Exception, Id: " + wrapId);
    }
    public WrapNotFoundException(String wrapId) {
        super("Order Not Found Exception, Id: " + wrapId);
    }
}
