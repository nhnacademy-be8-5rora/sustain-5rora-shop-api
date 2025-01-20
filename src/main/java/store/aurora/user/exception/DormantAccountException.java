package store.aurora.user.exception;

public class DormantAccountException extends RuntimeException {
    public DormantAccountException(String message) {
        super(message);
    }
}
