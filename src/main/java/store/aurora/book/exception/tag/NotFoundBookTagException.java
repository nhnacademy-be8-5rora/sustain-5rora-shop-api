package store.aurora.book.exception.tag;

public class NotFoundBookTagException extends RuntimeException {
    public NotFoundBookTagException(Long bookTagId) {
        super(String.format("%d not found bookTagId", bookTagId));
    }
}
