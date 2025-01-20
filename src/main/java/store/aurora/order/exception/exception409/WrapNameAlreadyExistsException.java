package store.aurora.order.exception.exception409;

import store.aurora.common.exception.DataAlreadyExistsException;

public class WrapNameAlreadyExistsException extends DataAlreadyExistsException {
    public WrapNameAlreadyExistsException(String message) {
        super(message);
    }
}
