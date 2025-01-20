package store.aurora.book.exception.author;

import store.aurora.common.exception.DataConflictException;

public class AuthorLinkedToBooksException extends DataConflictException {
    public AuthorLinkedToBooksException(String message) {
        super(message);
    }
}
