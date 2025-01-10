package store.aurora.book.exception.api;

public class ApiResponseEmptyException extends RuntimeException {
    public ApiResponseEmptyException(String message) {
        super(message);
    }
}
