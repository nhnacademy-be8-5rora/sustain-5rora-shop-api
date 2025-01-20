package store.aurora.common.exception;

public abstract class ImageException extends RuntimeException {
    protected ImageException(String message) {
        super(message);
    }
}
