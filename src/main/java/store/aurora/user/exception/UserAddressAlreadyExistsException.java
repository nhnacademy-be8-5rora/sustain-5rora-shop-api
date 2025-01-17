package store.aurora.user.exception;

import store.aurora.common.exception.DataConflictException;

public class UserAddressAlreadyExistsException extends DataConflictException {
    public UserAddressAlreadyExistsException(String message) {
        super(message);
    }
}
