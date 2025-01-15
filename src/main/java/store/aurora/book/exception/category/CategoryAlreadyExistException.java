package store.aurora.book.exception.category;

import store.aurora.common.exception.DataAlreadyExistsException;

public class CategoryAlreadyExistException extends DataAlreadyExistsException {
    public CategoryAlreadyExistException(String message) {
        super(message);
    }
}
