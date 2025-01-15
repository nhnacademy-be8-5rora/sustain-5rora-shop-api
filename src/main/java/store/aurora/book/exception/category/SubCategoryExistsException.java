package store.aurora.book.exception.category;

import store.aurora.common.exception.DataLimitExceededException;

public class SubCategoryExistsException extends DataLimitExceededException {
    public SubCategoryExistsException(String message) {
        super(message);
    }
}
