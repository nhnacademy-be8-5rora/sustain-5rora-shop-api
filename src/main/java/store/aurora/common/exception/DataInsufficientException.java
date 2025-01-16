package store.aurora.common.exception;

public abstract class DataInsufficientException extends RuntimeException {
    protected DataInsufficientException(String message) {
        super(message);
    }
}