package store.aurora.user.exception;

import store.aurora.common.exception.DataNotFoundException;

public class NotFoundUserException extends DataNotFoundException {
    public NotFoundUserException(String userId) {
        super(String.format("%s not found user", userId));
    }
}
