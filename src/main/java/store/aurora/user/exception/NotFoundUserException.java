package store.aurora.user.exception;

public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException(String userId) {
        super(String.format("%s not found user", userId));
    }
}
