package store.aurora.book.exception.publisher;

import store.aurora.common.exception.DataConflictException;

public class PublisherAlreadyExistsException extends DataConflictException {
    public PublisherAlreadyExistsException(String message) {
        super(message);
    }
}
