package store.aurora.book.exception.tag;

import store.aurora.common.exception.DataAlreadyExistsException;

public class TagAlreadyExistException extends DataAlreadyExistsException {
    public TagAlreadyExistException(String message) {
        super(message);
    }
}
