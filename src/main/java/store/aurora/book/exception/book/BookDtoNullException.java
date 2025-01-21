package store.aurora.book.exception.book;

import store.aurora.common.exception.DtoNullException;

public class BookDtoNullException extends DtoNullException {
    public BookDtoNullException(String message) {
        super(message);
    }
}
