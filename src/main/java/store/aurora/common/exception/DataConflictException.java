package store.aurora.common.exception;

public abstract class DataConflictException extends RuntimeException {
    protected DataConflictException(String message) {super(message);}
}
