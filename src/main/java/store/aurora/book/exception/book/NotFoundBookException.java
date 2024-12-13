package store.aurora.book.exception.book;

public class NotFoundBookException extends RuntimeException {
    public NotFoundBookException(Long bookId) {
        super(String.format("%d is not found book", bookId));
    }
}
