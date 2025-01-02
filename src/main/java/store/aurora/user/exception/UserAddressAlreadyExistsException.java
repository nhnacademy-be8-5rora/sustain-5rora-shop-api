package store.aurora.user.exception;

import store.aurora.common.exception.DataAlreadyExistsException;

public class UserAddressAlreadyExistsException extends DataAlreadyExistsException {
    public UserAddressAlreadyExistsException(String message) {
        super(message);
    }
}
