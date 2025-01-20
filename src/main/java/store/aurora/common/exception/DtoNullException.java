package store.aurora.common.exception;

public abstract class DtoNullException extends RuntimeException {
     protected DtoNullException(String message) {
        super(message);
    }
}
