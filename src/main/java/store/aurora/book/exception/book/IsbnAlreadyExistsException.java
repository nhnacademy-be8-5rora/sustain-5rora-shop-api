package store.aurora.book.exception.book;

import store.aurora.common.exception.DataConflictException;

public class IsbnAlreadyExistsException extends DataConflictException {
    public IsbnAlreadyExistsException(String isbn) {
        super("이미 존재하는 ISBN입니다: " + isbn);
    }
}
