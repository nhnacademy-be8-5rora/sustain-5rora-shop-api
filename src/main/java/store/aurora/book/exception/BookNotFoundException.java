package store.aurora.book.exception;

import store.aurora.common.exception.DataNotFoundException;

public class BookNotFoundException extends DataNotFoundException {
    public BookNotFoundException(Long bookId) {
        super("bookId " + bookId + " not found");
    }
}