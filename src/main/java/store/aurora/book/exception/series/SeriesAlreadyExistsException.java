package store.aurora.book.exception.series;

import store.aurora.common.exception.DataConflictException;

public class SeriesAlreadyExistsException extends DataConflictException {
    public SeriesAlreadyExistsException(String message) {
        super(message);
    }
}
