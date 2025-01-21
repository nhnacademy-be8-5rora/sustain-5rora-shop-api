package store.aurora.order.exception.exception409;

import store.aurora.common.exception.DataConflictException;

public class WrapNameAlreadyExistsException extends DataConflictException {
    public WrapNameAlreadyExistsException(String message) {
        super(message);
    }
}
