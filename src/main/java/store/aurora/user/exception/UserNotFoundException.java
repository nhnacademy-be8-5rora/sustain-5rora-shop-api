package store.aurora.user.exception;

import store.aurora.common.exception.DataNotFoundException;

public class UserNotFoundException extends DataNotFoundException {
    public UserNotFoundException(String userId) {
        super(String.format("%s not found user", userId));
    }
}
