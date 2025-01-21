package store.aurora.book.exception.author;

import store.aurora.common.exception.DataConflictException;

public class AuthorRoleAlreadyExistsException extends DataConflictException {
    public AuthorRoleAlreadyExistsException(String message) {
        super(message);
    }
}
