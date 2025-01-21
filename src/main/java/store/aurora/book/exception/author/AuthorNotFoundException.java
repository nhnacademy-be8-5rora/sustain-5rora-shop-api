package store.aurora.book.exception.author;

import store.aurora.common.exception.DataNotFoundException;

public class AuthorNotFoundException extends DataNotFoundException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}
