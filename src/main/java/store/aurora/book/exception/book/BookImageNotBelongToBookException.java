package store.aurora.book.exception.book;

public class BookImageNotBelongToBookException extends RuntimeException {

    public BookImageNotBelongToBookException(Long bookId, Long imageId) {
        super("Image with ID " + imageId + " does not belong to the book with ID " + bookId + ".");
    }

    public BookImageNotBelongToBookException(String message) {
        super(message);
    }
}