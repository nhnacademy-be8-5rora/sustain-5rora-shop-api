package store.aurora.book.exception.publisher;

import store.aurora.common.exception.DataConflictException;

public class PublisherLinkedToBooksException extends DataConflictException {
    public PublisherLinkedToBooksException(String message) {
        super(message);
    }
}
