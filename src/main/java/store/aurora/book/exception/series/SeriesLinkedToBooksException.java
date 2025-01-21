package store.aurora.book.exception.series;

import store.aurora.common.exception.DataConflictException;

public class SeriesLinkedToBooksException extends DataConflictException {
    public SeriesLinkedToBooksException(String message) {
        super(message);
    }
}
