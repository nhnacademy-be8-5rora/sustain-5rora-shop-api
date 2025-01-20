package store.aurora.book.exception.category;

import store.aurora.common.exception.DataConflictException;

public class CategoryAlreadyExistException extends DataConflictException {
    public CategoryAlreadyExistException(String message) {
        super(message);
    }
}
