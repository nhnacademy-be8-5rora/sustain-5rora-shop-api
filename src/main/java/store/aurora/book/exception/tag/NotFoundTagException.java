package store.aurora.book.exception.tag;

public class NotFoundTagException extends RuntimeException {
    public NotFoundTagException(Long tagId) {
        super(String.format("%d is not found tag", tagId));
    }
}
