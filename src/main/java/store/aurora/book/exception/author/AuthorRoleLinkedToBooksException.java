package store.aurora.book.exception.author;

import store.aurora.common.exception.DataConflictException;

public class AuthorRoleLinkedToBooksException extends DataConflictException {
    public AuthorRoleLinkedToBooksException(String message) {
        super(message);
    }
}
