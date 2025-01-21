package store.aurora.book.exception.author;

import store.aurora.common.exception.DataConflictException;

public class AuthorAlreadyExistsException extends DataConflictException {
    public AuthorAlreadyExistsException(String message) {
        super(message);
    }
}
