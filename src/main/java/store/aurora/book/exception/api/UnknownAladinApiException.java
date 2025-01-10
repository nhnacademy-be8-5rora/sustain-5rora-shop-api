package store.aurora.book.exception.api;

public class UnknownAladinApiException extends RuntimeException {
    public UnknownAladinApiException(String message) {
        super(message);
    }
    public UnknownAladinApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
