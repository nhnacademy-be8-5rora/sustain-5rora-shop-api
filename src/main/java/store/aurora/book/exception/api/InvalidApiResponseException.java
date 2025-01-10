package store.aurora.book.exception.api;

public class InvalidApiResponseException extends RuntimeException {
    public InvalidApiResponseException(String message) {
        super(message);
    }
    public InvalidApiResponseException(String message, Throwable cause) {
        super(message, cause);
    }

}
