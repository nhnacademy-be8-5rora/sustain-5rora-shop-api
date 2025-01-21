package store.aurora.user.exception;

public class DeletedAccountException extends RuntimeException {
    public DeletedAccountException(String message) {
        super(message);
    }
}
