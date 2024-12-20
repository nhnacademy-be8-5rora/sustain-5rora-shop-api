package store.aurora.user.exception;

public class AlreadyActiveUserException extends RuntimeException {
    public AlreadyActiveUserException(String message) {
        super(message);
    }
}
