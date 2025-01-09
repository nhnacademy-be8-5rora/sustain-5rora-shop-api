package store.aurora.book.exception.book;

import store.aurora.common.exception.DataAlreadyExistsException;

public class IsbnAlreadyExistsException extends DataAlreadyExistsException {
    public IsbnAlreadyExistsException(String isbn) {
        super("이미 존재하는 ISBN입니다: " + isbn);
    }
}
