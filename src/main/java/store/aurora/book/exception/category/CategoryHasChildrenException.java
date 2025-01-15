package store.aurora.book.exception.category;

import store.aurora.common.exception.DataLimitExceededException;

public class CategoryHasChildrenException extends DataLimitExceededException {
    public CategoryHasChildrenException(String message) {
        super(message);
    }
}
