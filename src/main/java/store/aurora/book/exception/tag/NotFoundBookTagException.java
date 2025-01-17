package store.aurora.book.exception.tag;

import store.aurora.common.exception.DataNotFoundException;

public class NotFoundBookTagException extends DataNotFoundException {
    public NotFoundBookTagException(Long bookTagId) {
        super(String.format("%d not found bookTagId", bookTagId));
    }
}
