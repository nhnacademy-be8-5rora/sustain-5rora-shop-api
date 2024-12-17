package store.aurora.book.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookId) {
        super("bookId " + bookId + " not found");
    }
}