package store.aurora.book.exception.author;

import store.aurora.common.exception.DataNotFoundException;

public class AuthorRoleNotFoundException extends DataNotFoundException {
    public AuthorRoleNotFoundException(String message) {
        super(message);
    }
}
