package store.aurora.review.exception;

import store.aurora.common.exception.DataConflictException;

public class ReviewAlreadyExistsException extends DataConflictException {
    public ReviewAlreadyExistsException(String message) {
        super(message);
    }
}
