package store.aurora.book.exception.tag;

import store.aurora.common.exception.DataConflictException;

public class TagAlreadyExistException extends DataConflictException {
    public TagAlreadyExistException(String message) {
        super(message);
    }
}
