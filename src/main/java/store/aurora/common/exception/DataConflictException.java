package store.aurora.common.exception;

public abstract class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
