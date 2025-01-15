package store.aurora.book.exception.category;

import store.aurora.common.exception.DataLimitExceededException;

public class CategoryLimitException extends DataLimitExceededException {
    public CategoryLimitException(String message) {
        super(message);
    }
}

