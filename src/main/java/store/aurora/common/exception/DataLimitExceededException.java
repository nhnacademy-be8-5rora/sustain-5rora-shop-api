package store.aurora.common.exception;

public abstract class DataLimitExceededException extends RuntimeException {
    protected DataLimitExceededException(String message) {
        super(message);
    }
}