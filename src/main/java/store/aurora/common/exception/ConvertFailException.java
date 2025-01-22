package store.aurora.common.exception;

public abstract class ConvertFailException extends RuntimeException{
    public ConvertFailException(String message) {
        super(message);
    }
}
