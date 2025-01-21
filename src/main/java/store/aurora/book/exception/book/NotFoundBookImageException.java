package store.aurora.book.exception.book;

import store.aurora.common.exception.DataNotFoundException;

public class NotFoundBookImageException extends DataNotFoundException {
  public NotFoundBookImageException(Long imageId) {
    super("Book image with ID " + imageId + " not found.");
  }

  public NotFoundBookImageException(String message) {
    super(message);
  }
}
