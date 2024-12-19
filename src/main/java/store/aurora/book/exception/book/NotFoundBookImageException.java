package store.aurora.book.exception.book;

public class NotFoundBookImageException extends RuntimeException {
  public NotFoundBookImageException(Long imageId) {
    super("Book image with ID " + imageId + " not found.");
  }

  public NotFoundBookImageException(String message) {
    super(message);
  }
}
