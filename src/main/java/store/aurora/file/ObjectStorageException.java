package store.aurora.file;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileStorageException extends RuntimeException {
  private final HttpStatus status;

  public FileStorageException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public FileStorageException(String message, Throwable cause, HttpStatus status) {
    super(message, cause);
    this.status = status;
  }
}
