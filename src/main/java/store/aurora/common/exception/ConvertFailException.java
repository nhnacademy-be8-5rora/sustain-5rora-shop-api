package store.aurora.common.exception;

public abstract class ConvertFailException extends RuntimeException{
    public ConvertFailException() {
    }

    public ConvertFailException(String message) {
        super(message);
    }

    public ConvertFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertFailException(Throwable cause) {
        super(cause);
    }
}
