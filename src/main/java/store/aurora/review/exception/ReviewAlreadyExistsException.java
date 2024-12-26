package store.aurora.review.exception;

import store.aurora.common.exception.DataAlreadyExistsException;

public class ReviewAlreadyExistsException extends DataAlreadyExistsException {
    public ReviewAlreadyExistsException(String message) {
        super(message);
    }
}
